package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.VoiceCommand
import bot.boobbot.utils.Utils.getRandomMoan

@CommandProperties(description = "moans \uD83D\uDE2B", nsfw = true, category = Category.AUDIO, guildOnly = true)
class Moan : VoiceCommand {
    override fun execute(ctx: Context) {
        if (!performVoiceChecks(ctx)) {
            return
        }

        if (!ctx.guild!!.audioManager.isConnected) {
            ctx.guild.audioManager.openAudioConnection(ctx.member!!.voiceState!!.channel)
        }

        playerManager.loadItem(getRandomMoan().toString(), AudioLoader(ctx))
    }
}
