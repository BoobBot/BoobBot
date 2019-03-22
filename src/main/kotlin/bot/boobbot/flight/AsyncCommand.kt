package bot.boobbot.flight

import bot.boobbot.BoobBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

interface AsyncCommand : Command {

    suspend fun executeAsync(ctx: Context)

    override fun execute(ctx: Context) {
        GlobalScope.async {
            try {
                executeAsync(ctx)
            } catch (e: Exception) {
                BoobBot.log.error("Command `${this@AsyncCommand.name}` encountered an error during execution", e)
                ctx.message.react("\uD83D\uDEAB")
            }
        }
    }

}
