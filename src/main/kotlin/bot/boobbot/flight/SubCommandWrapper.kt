package bot.boobbot.flight

import bot.boobbot.BoobBot
import java.lang.reflect.Method
import kotlin.coroutines.suspendCoroutine

class SubCommandWrapper(
    val name: String,
    val async: Boolean,
    val method: Method,
    private val kls: Command
) {

    fun execute(ctx: Context, vararg additionalArgs: Any?) {
        try {
            method.invoke(kls, ctx, *additionalArgs)
        } catch (e: Throwable) {
            BoobBot.log.error("Error in subcommand $name", e)
            ctx.message.addReaction("\uD83D\uDEAB").queue()
        }
    }

    suspend fun executeAsync(ctx: Context, vararg additionalArgs: Any?) {
        suspendCoroutine<Unit> {
            method.invoke(kls, ctx, *additionalArgs, it)
        }
    }

}