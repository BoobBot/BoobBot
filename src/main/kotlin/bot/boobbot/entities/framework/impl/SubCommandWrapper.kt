package bot.boobbot.entities.framework.impl

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.Option
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
    val options: List<Option>,
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

                    if (!ctx.isSlashContext) {
                        ctx.react(Emoji.fromUnicode("\uD83D\uDEAB"))
                    }
                }
            }
        } else {
            try {
                method.invoke(kls, ctx, *additionalArgs)
            } catch (e: Throwable) {
                BoobBot.log.error("Error in subcommand $name", e)

                if (!ctx.isSlashContext) {
                    ctx.react(Emoji.fromUnicode("\uD83D\uDEAB"))
                }
            }
        }
    }

    private suspend fun executeAsync(ctx: Context, vararg additionalArgs: Any?) {
        suspendCoroutine<Unit> {
            method.invoke(kls, ctx, *additionalArgs, it)
        }
    }

}