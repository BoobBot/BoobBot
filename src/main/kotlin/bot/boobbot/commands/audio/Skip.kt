package bot.boobbot.commands.audio

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.interfaces.VoiceCommand
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats

@CommandProperties(
    description = "Skips current playing track",
    category = Category.AUDIO,
    guildOnly = true,
    groupByCategory = true
)
class Skip : VoiceCommand {
    override fun execute(ctx: Context) {
        if (!performVoiceChecks(ctx)) {
            return
        }

        val player = ctx.audioPlayer
        val track = player.player.playingTrack
            ?: return ctx.reply(Formats.error("Wtf whore, How can i skip when im not playing anything?!"))

        if (!canSkip(ctx)) {
            return ctx.reply(Formats.error("No whore, i can't let you do that"))
        }

        player.playNext()

        if (player.player.playingTrack != null) {
            return ctx.reply {
                setColor(Colors.getEffectiveColor(ctx.member))
                addField(
                    Formats.info(" Track Skipped"),
                    "Skipped: ${track.info.title}\nNow Playing: ${player.player.playingTrack.info.title}",
                    false
                )
            }
        }

        ctx.reply {
            setColor(Colors.getEffectiveColor(ctx.member))
            addField(Formats.info(" Track Skipped"), "Skipped: ${track.info.title}", false)
        }
    }
}
