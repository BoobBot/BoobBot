package bot.boobbot.commands.audio

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.models.VoiceCommand

@CommandProperties(description = "Skips current playing track", category = Category.AUDIO, guildOnly = true, nsfw = true)
class Skip : VoiceCommand {

    override fun execute(ctx: Context) {
        val shouldPlay = performVoiceChecks(ctx)

        if (!shouldPlay) {
            return
        }

        val player = ctx.audioPlayer!!
        val track = player.player.playingTrack
                ?: return ctx.send(Formats.error("Wtf whore, How can i skip when im not playing anything?!"))

        if (!canSkip(ctx)) {
            return ctx.send(Formats.error("No whore, i can't let you do that"))
        }

        player.playNext()

        if (player.queue.size >= 1) {
            val nextTrack = player.queue[0]
            return ctx.embed {
                setColor(Colors.getDominantColor(ctx.author))
                addField(Formats.info(""), "Skipped: ${track.info.title}\nNow Playing: ${nextTrack.info.title}", false)
            }
        }
        ctx.embed {
            setColor(Colors.getDominantColor(ctx.author))
            addField(Formats.info(""), "Skipped: ${track.info.title}", false)
        }

    }

}
