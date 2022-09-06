package bot.boobbot.entities.framework.interfaces

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.MessageContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.emoji.Emoji

interface AsyncCommand : Command {
    suspend fun executeAsync(ctx: MessageContext)

    @Suppress("DeferredResultUnused", "EXPERIMENTAL_API_USAGE")
    override fun execute(ctx: MessageContext) {
        // Use IO dispatcher as most if not all of our suspend calls are for network requests.
        IoScope.launch {
            try {
                executeAsync(ctx)
            } catch (e: Exception) {
                BoobBot.log.error("Command `${this@AsyncCommand.name}` encountered an error during execution", e)
                ctx.message.addReaction(Emoji.fromUnicode("\uD83D\uDEAB")).queue()
            }
        }
    }

    companion object {
        private val IoScope = CoroutineScope(Dispatchers.IO)
    }
}