package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.getMusicManager
import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Utils.Companion.connectToVoiceChannel
import bot.boobbot.utils.Utils.Companion.getRandomMoan
import bot.boobbot.entities.framework.VoiceCommand

@CommandProperties(description = "moans :tired_face:", nsfw = true, category = Category.AUDIO, guildOnly = true)
class Moan : VoiceCommand {

    override fun execute(ctx: Context) {
        val shouldPlay = performVoiceChecks(ctx)

        if (!shouldPlay) {
            return
        }

        val musicManager = getMusicManager(ctx.message.guild)
        connectToVoiceChannel(ctx.message)
        playerManager.loadItemOrdered(musicManager, getRandomMoan().toString(), AudioLoader(musicManager, ctx))
    }

}