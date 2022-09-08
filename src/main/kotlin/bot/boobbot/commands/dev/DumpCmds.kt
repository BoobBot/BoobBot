package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
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

        println(categorised.size)
        println(remaining.size)

        ctx.reply(FileUpload.fromData(json.toByteArray(Charsets.UTF_8), "commands.json"))
    }

    private fun buildCommand(cmd: ExecutableCommand): CommandData {
        val slash = Commands.slash(cmd.name.lowercase(), cmd.properties.description).also {
            it.isGuildOnly = cmd.properties.guildOnly
        }

        when {
            cmd.subcommands.isNotEmpty() -> {
                for (sc in cmd.subcommands.values) {
                    val data = SubcommandData(sc.name, sc.description)

                    for (option in sc.options) {
                        val od = OptionData(option.type, option.name, option.description, option.required)

                        for (choice in option.choices) {
                            od.addChoice(choice.name, choice.value)
                        }

                        data.addOptions(od)
                    }

                    slash.addSubcommands(data)
                }
            }
            cmd.options.isNotEmpty() -> {
                for (option in cmd.options) {
                    val data = OptionData(option.type, option.name, option.description, option.required)

                    for (choice in option.choices) {
                        data.addChoice(choice.name, choice.value)
                    }

                    slash.addOptions(data)
                }
            }
        }

        return slash
    }

    private fun buildCategory(entry: Map.Entry<String, List<ExecutableCommand>>): CommandData {
        val (category, cmds) = entry
        val slash = Commands.slash(category, "$category commands")

        if (cmds.size > 25) {
            throw IllegalArgumentException("Cannot have more than 25 subcommands/groups per command!")
        }

        for (cmd in cmds) {
            if (cmd.subcommands.isNotEmpty()) {
                val group = SubcommandGroupData(cmd.name, cmd.properties.description)

                for (sc in cmd.subcommands.values) {
                    group.addSubcommands(buildSubcommand(sc))
                }

                slash.addSubcommandGroups(group)
                continue
            }

            val sc = SubcommandData(cmd.name, cmd.properties.description)

            for (option in cmd.options) {
                val data = OptionData(option.type, option.name, option.description, option.required)

                for (choice in option.choices) {
                    data.addChoice(choice.name, choice.value)
                }

                sc.addOptions(data)
            }

            slash.addSubcommands(sc)
        }

        return slash
    }

    private fun buildSubcommand(cmd: SubCommandWrapper): SubcommandData {
        val subcommand = SubcommandData(cmd.name.lowercase(), cmd.description)

        for (option in cmd.options) {
            val data = OptionData(option.type, option.name, option.description, option.required)

            for (choice in option.choices) {
                data.addChoice(choice.name, choice.value)
            }

            subcommand.addOptions(data)
        }

        return subcommand
    }
}
