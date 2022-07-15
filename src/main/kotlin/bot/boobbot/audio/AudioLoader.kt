package bot.boobbot.audio

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Utils
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
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

    override fun noMatches() = ctx.send("No matches, tf?")

    override fun loadFailed(e: FriendlyException) {
        ctx.send("Shit, track loading failed:\n`${e.localizedMessage}")
        BoobBot.log.error("Track loading failed", e)
    }

    private fun enqueueTrack(track: AudioTrack) {
        track.userData = ctx.author
        musicManager.addToQueue(track)
        when (val source = track.sourceManager.sourceName) {
            "local" -> ctx.send(":tired_face:")
            "pornhub" -> send(track, pornhubIcon)
            "redtube" -> send(track, redtubeIcon)
            "youtube" -> send(track, youtubeIcon)
            else -> ctx.send("`$source` is unsupported, whore")
        }
    }

    private fun send(track: AudioTrack, trackIcon: String) {
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.send {
            setColor(Colors.getEffectiveColor(ctx.message))
            setAuthor("Music", track.info.uri, trackIcon)
            addField(
                "Enqueued Track",
                "**Title**: ${track.info.title}\n**Duration**: ${Utils.fTime(track.info.length)}\n**Link**: ${track.info.uri}",
                false
            )
            setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
            setTimestamp(Instant.now())
            build()
        }
    }
}
