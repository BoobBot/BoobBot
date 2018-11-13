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
import java.util.*


class AudioLoader(private val musicManager: GuildMusicManager, private val ctx: Context) : AudioLoadResultHandler {

    override fun trackLoaded(track: AudioTrack) {
        enqueueTrack(track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        if (playlist.isSearchResult) {
            if (playlist.tracks[0].sourceManager.sourceName == "pornhub") {
                val randomIndex = Random().nextInt(playlist.tracks.size)
                enqueueTrack(playlist.tracks[randomIndex])
            } else {
                enqueueTrack(playlist.tracks[0])
            }
        }
    }

    override fun noMatches() {
        ctx.send("No matches, tf?")
    }


    override fun loadFailed(e: FriendlyException) {
        BoobBot.log.error("wot", e)
    }

    private fun enqueueTrack(track: AudioTrack) {
        track.userData = ctx.author
        musicManager.addToQueue(track)
        val source = track.sourceManager.sourceName
        when (source) {
            "local" -> ctx.message.channel.sendMessage(":tired_face:").queue()

            "pornhub" -> ctx.embed {
                setAuthor(
                    "PornHub is music too",
                    track.info.uri,
                    "https://data.apkhere.com/b2/com.app.pornhub/4.1.1/icon.png!s"
                )
                    .setColor(Colors.getEffectiveColor(ctx.message))
                    .addField(
                        "Enqueued track",
                        "**Title**: ${track.info.title}\n" +
                                "**Duration**: ${Utils.fTime(track.info.length)}\n" +
                                "**Link**: ${track.info.uri}",
                        false
                    )
                    .setFooter("Requested by ${ctx.author.name}", ctx.author.avatarUrl)
                    .setTimestamp(Instant.now())
                    .build()
            }

            "redtube" -> ctx.embed {
                setAuthor(
                    "RedTube is music too",
                    track.info.uri,
                    "https://cdn.discordapp.com/attachments/440667148315262978/490353839577497623/rt.png"
                )
                    .setColor(Colors.getEffectiveColor(ctx.message))
                    .addField(
                        "Enqueued track",
                        "**Title**: ${track.info.title}\n" +
                                "**Duration**: ${Utils.fTime(track.info.length)}\n" +
                                "**Link**: ${track.info.uri}",
                        false
                    )
                    .setFooter("Requested by ${ctx.author.name}", ctx.author.avatarUrl)
                    .setTimestamp(Instant.now())
                    .build()
            }

            "youtube" -> ctx.embed {
                setAuthor(
                    "Music",
                    track.info.uri,
                    "https://media.discordapp.net/attachments/440667148315262978/501803781130813450/kisspng-youtube-play-button-logo-computer-icons-youtube-icon-app-logo-png-5ab067d2053a02.15273601152.png?width=300&height=300"
                )
                    .setColor(Colors.getEffectiveColor(ctx.message))
                    .addField(
                        "Enqueued track",
                        "**Title**: ${track.info.title}\n" +
                                "**Duration**: ${Utils.fTime(track.info.length)}\n" +
                                "**Link**: ${track.info.uri}",
                        false
                    )
                    .setFooter("Requested by ${ctx.author.name}", ctx.author.avatarUrl)
                    .setTimestamp(Instant.now())
                    .build()

            }

            else -> {
                BoobBot.log.warn("Wtf am i playing? ${ctx.message.contentRaw} $source ${ctx.author}")
            }
        }
    }
}
