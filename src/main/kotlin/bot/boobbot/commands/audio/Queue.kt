package bot.boobbot.commands.audio

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.VoiceCommand
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.User
import org.jetbrains.kotlin.utils.addToStdlib.sumByLong
import java.time.Instant
import java.util.*

@CommandProperties(
    description = "Shows now playing and queue",
    aliases = ["q", "np", "nowplaying"],
    category = Category.AUDIO,
    guildOnly = true,
    nsfw = true
)
class Queue : VoiceCommand {
    override fun execute(ctx: Context) {
        if (!performVoiceChecks(ctx)) {
            return
        }

        val player = ctx.audioPlayer
        val track = player.player.playingTrack
            ?: return ctx.send(Formats.info("Im not playing anything? Play something or fuck off"))

        val queue = player.queue
        val queueTime = Utils.fTime(queue.sumByLong { it.duration })
        val queueStr = if (queue.isEmpty()) {
            "*Queue is empty.*"
        } else {
            queue.joinToString(separator = "\n", limit = 5, truncated = "Showing 5 of ${queue.size}") {
                """
                    **Title**: ${abbreviate(getTrackTitle(it), 50)}
                    **Duration:** ${Utils.fTime(it.info.length)}
                    **Source:** ${getTrackSource(it)}
                    **Requester:** ${it.getUserData(User::class.java).asTag}
                """.trimIndent()
            }
        }

        ctx.send {
            setAuthor("Queue | ${ctx.guild!!.name}", BoobBot.inviteUrl, ctx.selfUser.effectiveAvatarUrl)
            setColor(Colors.getEffectiveColor(ctx.message))
            addField(
                "Current", """
                **Title:** ${abbreviate(getTrackTitle(track))}
                **Duration:** ${Utils.fTime(track.info.length)}
                **Source:** ${getTrackSource(track)}
                **Requester:** ${track.getUserData(User::class.java).asTag}
            """.trimIndent(), false
            )
            addField("Queue", queueStr, false)
            setFooter("Total duration $queueTime", ctx.selfUser.effectiveAvatarUrl)
            setTimestamp(Instant.now())
            build()
        }
    }

    private fun getTrackTitle(track: AudioTrack) = when (track) {
        is LocalAudioTrack -> "Moan 😩"
        else -> track.info.title
    }

    private fun getTrackSource(track: AudioTrack) = when (track) {
        is LocalAudioTrack -> "Moan"
        else -> track.sourceManager.sourceName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    private fun abbreviate(s: String, maxChars: Int = 50) = when {
        s.length > maxChars - 3 -> s.substring(0, maxChars - 3) + "..."
        else -> s
    }
}
