package bot.boobbot.flight

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.dv8tion.jda.core.entities.Message

class ExecutableCommand(
    private val cmd: Command,
    public val subcommands: Map<String, SubCommandWrapper>
) {

    public val name = cmd.name
    public val hasProperties = cmd.hasProperties
    public val properties = cmd.properties

    fun execute(trigger: String, message: Message, args: MutableList<String>) {
        if (args.isNotEmpty() && subcommands.containsKey(args[0])) {
            val subcommand = subcommands.getValue(args[0])
            val ctx = Context(trigger, message, args.drop(1).toTypedArray())

            if (subcommand.async) {
                GlobalScope.async {
                    subcommand.executeAsync(ctx)
                }
            } else {
                subcommand.execute(ctx)
            }

            return
        }

        cmd.execute(Context(trigger, message, args.toTypedArray()))
    }

}