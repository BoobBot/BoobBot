package bot.boobbot.entities.framework

import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import net.dv8tion.jda.api.entities.Message

class ExecutableCommand(
    private val cmd: Command,
    val subcommands: Map<String, SubCommandWrapper>
) {

    val name = cmd.name
    val hasProperties = cmd.hasProperties
    val properties = cmd.properties

    fun getSubCommand(key: String?): SubCommandWrapper? {
        if (key == null) {
            return null
        }

        return subcommands[key]
            ?: subcommands.values.firstOrNull { it.aliases.contains(key) }
    }

    fun execute(trigger: String, message: Message, args: MutableList<String>) {
        val subcommand = getSubCommand(args.firstOrNull())

        val ctx = if (subcommand != null) {
            Context(trigger, message, args.drop(1))
        } else {
            Context(trigger, message, args)
        }

        if (!cmd.localCheck(ctx)) {
            return
        }

        if (subcommand != null) {
            if (subcommand.donorOnly && !Utils.checkDonor(ctx.message)) {
                ctx.send(
                    Formats.error(
                        " Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> "
                                + "Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"
                    )
                )
                return
            }
            subcommand.execute(ctx)
        } else {
            cmd.execute(ctx)
        }
    }

}