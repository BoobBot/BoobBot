package bot.boobbot.entities.framework.interfaces

import bot.boobbot.BoobBot
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent

interface AsyncUserContextCommand : UserContextCommand {

    suspend fun executeAsync(event: UserContextInteractionEvent)

    @OptIn(DelicateCoroutinesApi::class)
    @Suppress("DeferredResultUnused", "EXPERIMENTAL_API_USAGE")
    override fun execute(event: UserContextInteractionEvent) {
        GlobalScope.async {
            try {
                executeAsync(event)
            } catch (e: Exception) {
                BoobBot.log.error("Command `${this@AsyncUserContextCommand.name}` encountered an error during execution", e)
                event.reply("\uD83D\uDEAB broken? dunno, report code 42 to https://discord.boob.bot").queue()
            }
        }
    }

}
