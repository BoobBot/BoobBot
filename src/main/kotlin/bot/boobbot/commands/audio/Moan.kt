package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.VoiceCommand
import bot.boobbot.utils.Utils.Companion.connectToVoiceChannel
import bot.boobbot.utils.Utils.Companion.getRandomMoan

@CommandProperties(description = "moans :tired_face:", nsfw = true, category = Category.AUDIO, guildOnly = true, enabled = false)
class Moan : VoiceCommand {
    override fun execute(ctx: Context) {
        if (!performVoiceChecks(ctx)) {
            return
        }

        connectToVoiceChannel(ctx.message)
        playerManager.loadItem(getRandomMoan().toString(), AudioLoader(ctx))
    }
}
