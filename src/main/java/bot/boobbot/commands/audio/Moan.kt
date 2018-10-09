package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.getMusicManager
import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils.Companion.connectToVoiceChannel
import bot.boobbot.misc.Utils.Companion.getRandomMoan
import bot.boobbot.models.VoiceCommand
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.utils.PermissionUtil

@CommandProperties(description = "moans :tired_face:", nsfw = true, category = CommandProperties.category.AUDIO)
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