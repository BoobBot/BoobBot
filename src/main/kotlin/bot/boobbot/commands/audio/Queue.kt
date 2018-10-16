package bot.boobbot.commands.audio

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import bot.boobbot.models.VoiceCommand
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.User
import org.apache.commons.lang3.StringUtils
import java.time.Instant

@CommandProperties(description = "Shows now playing and queue", aliases = ["q", "np", "nowplaying"], category = Category.AUDIO, guildOnly = true, nsfw = true)
class Queue : VoiceCommand {

    override fun execute(ctx: Context) {
        val shouldPlay = performVoiceChecks(ctx)

        if (!shouldPlay) {
            return
        }

        val player = ctx.audioPlayer!!
        val track = player.player.playingTrack
                ?: return ctx.send(Formats.info("Im not playing anything? Play something or fuck off"))

        val q = player.queue
        val qStr = if (q.size >= 1){
            q.joinToString(separator = "\n", limit = 5, truncated = "Showing 5 of ${q.size}") {
                "**Title**: ${StringUtils.abbreviate(it.info.title.replace("Unknown title", "Moan :tired_face:"), 20)}" +
                        " **Duration**: ${Utils.fTime(it.info.length)}" +
                        " **Source**: ${it.sourceManager.sourceName.replace("local", "moan")}" +
                        " **User**: ${(it.userData as User).name}"
            }
        } else{
            "Nothing Queued"
        }

        val total = Utils.fTime(q.asSequence().map { it.duration }.sum())

        ctx.embed {
            setAuthor("Current playlist",
                    ctx.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR),
                    ctx.jda.selfUser.avatarUrl)
                    .setColor(Colors.getEffectiveColor(ctx.message))
                    .addField("Now Playing",
                            "**Title**: ${StringUtils.abbreviate(track.info.title.replace("Unknown title", "Moan :tired_face:"), 20)}" +
                                    " **Duration**: ${Utils.fTime(track.info.length)}" +
                                    " **Source**: ${track.sourceManager.sourceName.replace("local", "moan")}" +
                                    " **User**: ${(track.userData as User).name}", false
                    )
                    .addField("Queue",
                            qStr,
                            false)
                    .setFooter("Total duration $total", ctx.jda.selfUser.avatarUrl)
                    .setTimestamp(Instant.now())
                    .build()
        }

    }

}
