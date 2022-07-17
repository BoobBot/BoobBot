package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.VoiceCommand
import bot.boobbot.utils.Utils.getRandomMoan
import net.dv8tion.jda.api.entities.VoiceChannel

@CommandProperties(description = "moans \uD83D\uDE2B", nsfw = true, category = Category.AUDIO, guildOnly = true)
class Moan : VoiceCommand {
    override fun execute(ctx: Context) {
        if (!performVoiceChecks(ctx, nsfwCheck = true)) {
            return
        }

        playerManager.loadItem(getRandomMoan().toString(), AudioLoader(ctx))
    }
}
