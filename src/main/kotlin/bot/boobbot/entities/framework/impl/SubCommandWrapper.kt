package bot.boobbot.entities.framework.impl

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.interfaces.Command
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.dv8tion.jda.api.entities.emoji.Emoji
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
    fun execute(ctx: MessageContext, vararg additionalArgs: Any?) {
        if (async) {
            GlobalScope.async {
                try {
                    executeAsync(ctx)
                } catch (e: Throwable) {
                    BoobBot.log.error("Error in subcommand $name", e)
                    ctx.message.addReaction(Emoji.fromUnicode("\uD83D\uDEAB")).queue()
                }
            }
        } else {
            try {
                method.invoke(kls, ctx, *additionalArgs)
            } catch (e: Throwable) {
                BoobBot.log.error("Error in subcommand $name", e)
                ctx.message.addReaction(Emoji.fromUnicode("\uD83D\uDEAB")).queue()
            }
        }
    }

    private suspend fun executeAsync(ctx: MessageContext, vararg additionalArgs: Any?) {
        suspendCoroutine<Unit> {
            method.invoke(kls, ctx, *additionalArgs, it)
        }
    }

}