package bot.boobbot.entities.framework.impl

import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import net.dv8tion.jda.api.entities.Message

class ExecutableCommand(
    private val cmd: Command,
    val subcommands: Map<String, SubCommandWrapper>,
    val slashEnabled: Boolean,
    val category: String?
) {
    val name = cmd.name
    val properties = cmd.properties

    fun getSubCommand(key: String?): SubCommandWrapper? {
        if (key == null) {
            return null
        }

        return subcommands[key]
            ?: subcommands.values.firstOrNull { it.aliases.contains(key) }
    }

    fun execute(message: Message, args: MutableList<String>) {
        val subcommand = getSubCommand(args.firstOrNull())

        val ctx = if (subcommand != null) {
            MessageContext(message, args.drop(1))
        } else {
            MessageContext(message, args)
        }

        if (!cmd.localCheck(ctx)) {
            return
        }

        if (subcommand != null) {
            if (subcommand.donorOnly && !Utils.checkDonor(ctx.message)) {
                return ctx.reply(Formats.error(" Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"))
            }
            subcommand.execute(ctx)
        } else {
            cmd.execute(ctx)
        }
    }

}