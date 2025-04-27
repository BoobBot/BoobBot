package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.data.DataArray

@CommandProperties(description = "Get all commands as JSON.", category = Category.DEV, developerOnly = true, groupByCategory = true)
class DumpCmds : Command {
    override fun execute(ctx: Context) {
        sendSubcommandHelp(ctx)
    }

    @SubCommand(description = "Sends a JSON of all command trees.")
    fun all(ctx: Context) {
        val json = BoobBot.commands.export()
            .map { it.toData() }
            .let(DataArray::fromCollection)
            .toPrettyString()

        ctx.reply(FileUpload.fromData(json.toByteArray(Charsets.UTF_8), "commands.json"))
    }

    @SubCommand(description = "Sync command list to Discord.")
    fun sync(ctx: Context) {
        val slashCommands = BoobBot.commands.export()
        val contextCommands = BoobBot.userContextCommands.export()

        ctx.jda.updateCommands()
            .addCommands(slashCommands)
            .addCommands(contextCommands)
            .queue(
                { ctx.reply("commands re-synced with discord") },
                { ctx.reply("sync failed (`${it.localizedMessage}`)"); it.printStackTrace() }
            )
    }

    @SubCommand(description = "Trace command build information.")
    @Option(name = "command", description = "Command name to diagnose.")
    @Option(name = "subcommand", description = "Subcommand name to diagnose.", required = false)
    fun trace(ctx: Context) {
        val command = ctx.options.getByNameOrNext("command", Resolver.STRING)
            ?: return ctx.reply("Wtf, specify a command whore.")
        val subcommand = ctx.options.getByNameOrNext("subcommand", Resolver.STRING)

        val cmd = BoobBot.commands.findCommand(command)
            ?: return ctx.reply("Wtf, I couldn't find a command with that name, whore.")
        val sc = subcommand?.let { cmd.subcommands[subcommand] }

        val cmdData = BoobBot.commands.buildCommand(cmd).toData().toPrettyString()
        val scData = sc?.let { BoobBot.commands.buildSubcommand(it) }?.toData()?.toPrettyString()

        val files = mutableListOf(
            FileUpload.fromData(cmdData.toByteArray(Charsets.UTF_8), "command.json")
        )

        if (scData != null) {
            files.add(FileUpload.fromData(scData.toByteArray(Charsets.UTF_8), "subcommand.json"))
        }

        ctx.reply(FileUpload.fromData(cmdData.toByteArray(Charsets.UTF_8), "command.json"))
    }
}
