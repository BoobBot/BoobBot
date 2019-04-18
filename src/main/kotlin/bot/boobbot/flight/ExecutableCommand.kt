package bot.boobbot.flight

import net.dv8tion.jda.core.entities.Message

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

        val sc = subcommands.filter { it.key == key || it.value.aliases.contains(key) }

        if (sc.isEmpty()) {
            return null
        }

        return sc.values.firstOrNull()
    }

    fun execute(trigger: String, message: Message, args: MutableList<String>) {
        val subcommand = getSubCommand(args.firstOrNull())

        val ctx = if (subcommand != null) {
            Context(trigger, message, args.drop(1).toTypedArray())
        } else {
            Context(trigger, message, args.toTypedArray())
        }

        if (!cmd.localCheck(ctx)) {
            return
        }

        if (subcommand != null) {
            subcommand.execute(ctx)
        } else {
            cmd.execute(ctx)
        }
    }

}