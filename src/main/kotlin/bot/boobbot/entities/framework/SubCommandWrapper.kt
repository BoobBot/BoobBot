package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.lang.reflect.Method
import kotlin.coroutines.suspendCoroutine

class SubCommandWrapper(
    val name: String,
    val aliases: Array<String>,
    val async: Boolean,
    val description: String,
    val donorOnly: Boolean,
    val method: Method,
    private val kls: Command
) {

    @Suppress("DeferredResultUnused", "EXPERIMENTAL_API_USAGE")
    fun execute(ctx: Context, vararg additionalArgs: Any?) {
        if (async) {
            GlobalScope.async {
                try {
                    executeAsync(ctx)
                } catch (e: Throwable) {
                    BoobBot.log.error("Error in subcommand $name", e)
                    ctx.message.addReaction("\uD83D\uDEAB").queue()
                }
            }
        } else {
            try {
                method.invoke(kls, ctx, *additionalArgs)
            } catch (e: Throwable) {
                BoobBot.log.error("Error in subcommand $name", e)
                ctx.message.addReaction("\uD83D\uDEAB").queue()
            }
        }
    }

    private suspend fun executeAsync(ctx: Context, vararg additionalArgs: Any?) {
        suspendCoroutine<Unit> {
            method.invoke(kls, ctx, *additionalArgs, it)
        }
    }

}