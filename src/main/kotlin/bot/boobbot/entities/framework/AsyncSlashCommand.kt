package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

interface AsyncSlashCommand : SlashCommand {

    suspend fun executeAsync(event: SlashCommandEvent)

    override fun execute(event: SlashCommandEvent) {
        GlobalScope.async {
            try {
                executeAsync(event)
            } catch (e: Exception) {
                BoobBot.log.error("Command `${this@AsyncSlashCommand.name}` encountered an error during execution", e)
                event.reply("\uD83D\uDEAB broken? dunno, report code 42 to https://discord.boob.bot").queue()
            }
        }
    }

}
