package bot.boobbot.audio

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Utils
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import io.sentry.Sentry
import io.sentry.event.Breadcrumb
import io.sentry.event.BreadcrumbBuilder
import io.sentry.event.Event
import io.sentry.event.EventBuilder
import io.sentry.event.interfaces.ExceptionInterface
import java.time.Instant

class AudioLoader(private val ctx: Context, private val musicManager: GuildMusicManager = ctx.audioPlayer) : AudioLoadResultHandler {

    private val youtubeIcon =
        "https://media.discordapp.net/attachments/440667148315262978/501803781130813450/kisspng-youtube-play-button-logo-computer-icons-youtube-icon-app-logo-png-5ab067d2053a02.15273601152.png?width=300&height=300"
    private val pornhubIcon = "https://data.apkhere.com/b2/com.app.pornhub/4.1.1/icon.png!s"
    private val redtubeIcon = "https://cdn.discordapp.com/attachments/440667148315262978/490353839577497623/rt.png"

    override fun playlistLoaded(playlist: AudioPlaylist) {
        if (playlist.isSearchResult) {
            if (playlist.tracks[0].sourceManager.sourceName == "pornhub") {
                enqueueTrack(playlist.tracks.random())
            } else {
                enqueueTrack(playlist.tracks[0])
            }
        }
    }

    override fun trackLoaded(track: AudioTrack) = enqueueTrack(track)

    override fun noMatches() = ctx.reply("No matches, tf?")

    override fun loadFailed(e: FriendlyException) {
        ctx.reply("Shit, track loading failed:\n`${e.localizedMessage}")
        BoobBot.log.error("Track loading failed", e)
    }

    private fun enqueueTrack(track: AudioTrack) {
        track.userData = ctx.user
        musicManager.addToQueue(track)
        when (val source = track.sourceManager.sourceName) {
            "local" -> ctx.reply(":tired_face:")
            "pornhub" -> send(track, pornhubIcon)
            "redtube" -> send(track, redtubeIcon)
            "youtube" -> send(track, youtubeIcon)
            else -> ctx.reply("`$source` is unsupported, whore")
        }
    }

    private fun send(track: AudioTrack, trackIcon: String) {
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        runCatching {
            ctx.reply {
                setColor(Colors.getEffectiveColor(ctx.member))
                setAuthor("Music", track.info.uri, trackIcon)
                addField(
                    "Enqueued Track",
                    "**Title**: ${track.info.title}\n**Duration**: ${Utils.fTime(track.info.length)}\n**Link**: ${track.info.uri}",
                    false
                )
                setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
                setTimestamp(Instant.now())
            }
        }.onFailure {
            val builder = EventBuilder()
                .withMessage(it.localizedMessage)
                .withLevel(Event.Level.ERROR)
                .withSentryInterface(ExceptionInterface(it))

            if (it is IllegalArgumentException) {
                val breadcrumb = BreadcrumbBuilder()
                    .setLevel(Breadcrumb.Level.INFO)
                    .withData("Track ID", track.identifier)
                    .withData("Track URL", track.info.uri)
                    .build()

                builder.withBreadcrumbs(listOf(breadcrumb))
            }

            Sentry.capture(builder)
        }
    }
}
