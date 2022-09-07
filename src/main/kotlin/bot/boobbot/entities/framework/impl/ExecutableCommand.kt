package bot.boobbot.entities.framework.impl

import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils

class ExecutableCommand(
    private val cmd: Command,
    val subcommands: Map<String, SubCommandWrapper>,
    val slashEnabled: Boolean,
    val category: String?
) {
    val name = cmd.name
    val properties = cmd.properties

    fun execute(ctx: Context) {
        if (!cmd.localCheck(ctx)) {
            return
        }

        when (ctx) {
            is MessageContext -> execute(ctx)
            is SlashContext -> execute(ctx)
        }
    }

    private fun execute(ctx: MessageContext) {
        val subcommand = ctx.options.raw().firstOrNull()?.let { subcommands[it] ?: subcommands.values.firstOrNull { sc -> sc.aliases.contains(it) } }

        if (subcommand != null) {
            ctx.options.dropNext()

            if (subcommand.donorOnly && !Utils.checkDonor(ctx)) {
                return ctx.reply(Formats.error(" Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"))
            }

            return subcommand.execute(ctx)
        }

        cmd.execute(ctx)
    }

    private fun execute(ctx: SlashContext) {
        val subcommand = ctx.event.subcommandName?.let(subcommands::get)

        if (subcommand != null) {
            if (subcommand.donorOnly && !Utils.checkDonor(ctx)) {
                return ctx.reply(Formats.error(" Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"))
            }

            return subcommand.execute(ctx)
        }

        cmd.execute(ctx)
    }

}