package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.Options
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.ExecutableCommand
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.impl.SubCommandWrapper
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.data.DataArray
import net.dv8tion.jda.api.utils.data.DataObject
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

@CommandProperties(description = "Get all commands as JSON.", category = Category.DEV, developerOnly = true, groupByCategory = true)
class DumpCmds : Command {
    override fun execute(ctx: Context) {
        val allSlashCommands = BoobBot.commands.values.filter { it.slashEnabled }
        val categorised = allSlashCommands.filter { it.category != null }.groupBy { it.category!! }.map(::buildCategory).map { it.toData() }
        val remaining = allSlashCommands.filter { it.category == null }.map(::buildCommand).map { it.toData() }

        val json = DataArray.empty()
            .addAll(categorised)
            .addAll(remaining)
            .toPrettyString()

        println("${categorised.size} categorised, ${remaining.size} non-categorised. ${categorised.size + remaining.size} commands total.")
        ctx.reply(FileUpload.fromData(json.toByteArray(Charsets.UTF_8), "commands.json"))
    }

    @SubCommand(description = "Sync command list to Discord.")
    fun sync(ctx: Context) {
        val allSlashCommands = BoobBot.commands.values.filter { it.slashEnabled }
        val categorised = allSlashCommands.filter { it.category != null }.groupBy { it.category!! }.map(::buildCategory)
        val remaining = allSlashCommands.filter { it.category == null }.map(::buildCommand)

        ctx.jda.updateCommands()
            .addCommands(categorised)
            .addCommands(remaining)
            .queue(
                { ctx.reply("commands re-synced with discord") },
                { ctx.reply("sync failed (`${it.localizedMessage}`)"); it.printStackTrace() }
            )
    }

    @SubCommand(description = "Trace command build information.")
    @Options([
        Option(name = "command", description = "Command name to diagnose."),
        Option(name = "subcommand", description = "Subcommand name to diagnose.", required = false)
    ])
    fun trace(ctx: Context) {
        val command = ctx.options.getByNameOrNext("command", Resolver.STRING)
            ?: return ctx.reply("Wtf, specify a command whore.")
        val subcommand = ctx.options.getByNameOrNext("subcommand", Resolver.STRING)

        val cmd = BoobBot.commands.findCommand(command)
            ?: return ctx.reply("Wtf, I couldn't find a command with that name, whore.")
        val sc = subcommand?.let { cmd.subcommands[subcommand] }

        val cmdData = buildCommand(cmd).toData().toPrettyString()
        val scData = sc?.let { buildSubcommand(it) }?.toData()?.toPrettyString()

        ctx.reply(FileUpload.fromData(cmdData.toByteArray(Charsets.UTF_8), "command.json"))

        if (scData != null) {
            ctx.reply(FileUpload.fromData(scData.toByteArray(Charsets.UTF_8), "subcommand.json"))
        }
    }

    private fun buildCommand(cmd: ExecutableCommand): SlashCommandData {
        return Commands.slash(cmd.name.lowercase(), cmd.properties.description)
            .also {
                it.isGuildOnly = cmd.properties.guildOnly
                it.isNSFW = cmd.properties.nsfw
                it.addOptions(buildOptions(cmd.options))
                it.addSubcommands(cmd.subcommands.values.map(::buildSubcommand))
            }
    }

    private fun buildCategory(entry: Map.Entry<String, List<ExecutableCommand>>): SlashCommandData {
        val (category, cmds) = entry

        if (cmds.size > 25) {
            throw IllegalArgumentException("Cannot have more than 25 subcommands/groups per command!")
        }

        val slash = Commands.slash(category, "$category commands")
            .also {
                it.isGuildOnly = entry.value.all { props -> props.properties.guildOnly }
                it.isNSFW = entry.value.any { props -> props.properties.nsfw }
            }

        for (cmd in cmds) {
            if (cmd.subcommands.isNotEmpty()) {
                val group = SubcommandGroupData(cmd.name, cmd.properties.description).also {
                    it.addSubcommands(cmd.subcommands.values.map(::buildSubcommand))
                }

                slash.addSubcommandGroups(group)
                continue
            }

            val sc = SubcommandData(cmd.name, cmd.properties.description)
                .also { it.addOptions(buildOptions(cmd.options)) }

            slash.addSubcommands(sc)
        }

        return slash
    }

    private fun buildSubcommand(cmd: SubCommandWrapper): SubcommandData {
        return SubcommandData(cmd.name.lowercase(), cmd.description)
            .addOptions(buildOptions(cmd.options))
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
