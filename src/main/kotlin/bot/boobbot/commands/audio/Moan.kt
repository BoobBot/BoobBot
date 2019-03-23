package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.catnip
import bot.boobbot.BoobBot.Companion.getMusicManager
import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Utils.Companion.getRandomMoan
import bot.boobbot.models.VoiceCommand

@CommandProperties(description = "moans :tired_face:", nsfw = true, category = Category.AUDIO, guildOnly = true)
class Moan : VoiceCommand {

    override fun execute(ctx: Context) {
//        val shouldPlay = performVoiceChecks(ctx)
//
//        if (!shouldPlay)
//            return
//        }
//
        val musicManager = getMusicManager(ctx.guild!!)
        val voiceState = catnip.cache().voiceState(ctx.guild.id(), ctx.message.author().id())
        catnip.openVoiceConnection(ctx.guild.id(), voiceState!!.channelId()!!)
        playerManager.loadItemOrdered(musicManager, getRandomMoan().toString(), AudioLoader(musicManager, ctx))
    }

}
