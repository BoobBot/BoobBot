package bot.boobbot.audio

import bot.boobbot.BoobBot
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Utils
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.time.Instant


class AudioLoader(private val musicManager: GuildMusicManager, private val ctx: Context) : AudioLoadResultHandler {

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

    override fun loadFailed(e: FriendlyException) = BoobBot.log.error("wot", e)

    private fun enqueueTrack(track: AudioTrack) {
        track.userData = ctx.author
        musicManager.addToQueue(track)
        val source = track.sourceManager.sourceName
        when (source) {
            "local" -> ctx.send(":tired_face:")
            "pornhub" -> send(track, pornhubIcon)
            "redtube" -> send(track, redtubeIcon)
            "youtube" -> send(track, youtubeIcon)
            else -> BoobBot.log.warn("Wtf am i playing? ${ctx.message.content()} $source ${ctx.author}")
        }
    }

    private fun send(track: AudioTrack, trackIcon: String) {
        ctx.embed {
            color(Colors.getEffectiveColor(ctx.message))
            author("Music", track.info.uri, trackIcon)
            field(
                "Enqueued Track",
                "**Title**: ${track.info.title}\n**Duration**: ${Utils.fTime(track.info.length)}\n**Link**: ${track.info.uri}",
                false
            )
            footer("Requested by ${ctx.author.username()}", ctx.author.effectiveAvatarUrl())
            timestamp(Instant.now())
            build()
        }
    }
}
