package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

interface AsyncSlashCommand : SlashCommand {

    suspend fun executeAsync(ctx: SlashContext)

    @Suppress("DeferredResultUnused", "EXPERIMENTAL_API_USAGE")
    override fun execute(ctx: SlashContext) {
        GlobalScope.async {
            try {
                executeAsync(ctx)
            } catch (e: Exception) {
                BoobBot.log.error("Command `${this@AsyncSlashCommand.name}` encountered an error during execution", e)
                ctx.reply("\uD83D\uDEAB broken? dunno, report code 42 to https://discord.boob.bot")
            }
        }
    }

}
