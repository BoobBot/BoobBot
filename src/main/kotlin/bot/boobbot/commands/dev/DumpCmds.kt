package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.ExecutableCommand
import bot.boobbot.entities.framework.impl.SubCommandWrapper
import bot.boobbot.entities.framework.interfaces.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.data.DataArray
import net.dv8tion.jda.api.utils.data.DataObject
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

@CommandProperties(description = "Get all commands as JSON.", category = Category.DEV, developerOnly = true)
class DumpCmds : Command {
    override fun execute(ctx: Context) {
        val allSlashCommands = BoobBot.commands.values.filter { it.slashEnabled }
        val categorised = allSlashCommands.filter { it.category != null }.groupBy { it.category!! }.map(::buildCategory)
        val remaining = allSlashCommands.filter { it.category == null }.map(::buildCommand)

        val json = DataArray.empty()
            .addAll(categorised)
            .addAll(remaining)
            .toPrettyString()

        println("${categorised.size} categorised, ${remaining.size} non-categorised. ${categorised.size + remaining.size} commands total.")

        ctx.reply(FileUpload.fromData(json.toByteArray(Charsets.UTF_8), "commands.json"))
    }

    private fun buildCommand(cmd: ExecutableCommand): DataObject {
        val isNsfw = cmd.properties.nsfw

        val slash = Commands.slash(cmd.name.lowercase(), cmd.properties.description)
            .also { it.isGuildOnly = cmd.properties.guildOnly }
            .also { buildOptions(cmd.options).also(it::addOptions) }
            .toData()
            .also { data -> isNsfw.ifTrue { data.put("nsfw", true) } }

        if (cmd.subcommands.isNotEmpty()) {
            slash.getArray("options").addAll(cmd.subcommands.values.map(::buildSubcommand))
        }

        return slash
    }

    private fun buildCategory(entry: Map.Entry<String, List<ExecutableCommand>>): DataObject {
        val (category, cmds) = entry

        if (cmds.size > 25) {
            throw IllegalArgumentException("Cannot have more than 25 subcommands/groups per command!")
        }

        val isNsfw = entry.value.any { it.properties.nsfw }
        val isGuildOnly = entry.value.all { it.properties.guildOnly }

        val slash = Commands.slash(category, "$category commands")
            .also { it.isGuildOnly = isGuildOnly }
            .toData()
            .also { data -> isNsfw.ifTrue { data.put("nsfw", true) } }

        for (cmd in cmds) {
            if (cmd.subcommands.isNotEmpty()) {
                val group = SubcommandGroupData(cmd.name, cmd.properties.description).toData()
                val scs = cmd.subcommands.values.map(::buildSubcommand)

                group.getArray("options").addAll(scs)
                slash.getArray("options").add(group)
                continue
            }

            val sc = SubcommandData(cmd.name, cmd.properties.description)
                .also { buildOptions(cmd.options).also(it::addOptions) }
                .toData()

            slash.getArray("options").add(sc)
        }

        return slash
    }

    private fun buildSubcommand(cmd: SubCommandWrapper): DataObject {
        return SubcommandData(cmd.name.lowercase(), cmd.description)
            .also { buildOptions(cmd.options).also(it::addOptions) }
            .toData()
    }

    private fun buildOptions(options: List<Option>): List<OptionData> {
        return options.map {
            OptionData(it.type, it.name, it.description, it.required).also { data ->
                for (choice in it.choices) {
                    data.addChoice(choice.name, choice.value)
                }
            }
        }
    }
}
