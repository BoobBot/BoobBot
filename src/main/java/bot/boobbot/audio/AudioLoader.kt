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
            val randomIndex = Random().nextInt(playlist.tracks.size)
            enqueueTrack(playlist.tracks[randomIndex])
        }
    }

    override fun noMatches() {
        ctx.send("No matches, tf?")
    }


    override fun loadFailed(e: FriendlyException) {
        BoobBot.log.error("wot", e) //should not happen rn all local tracks
    }

    private fun enqueueTrack(track: AudioTrack) {
        track.userData = ctx.author
        musicManager.addToQueue(track)
        BoobBot.log.info(track.sourceManager.sourceName)
        if (track.sourceManager.sourceName == "local") {
            ctx.message.channel.sendMessage(":tired_face:").queue()//{ m -> m.delete().queueAfter(5, TimeUnit.SECONDS) }, null)
        }
        if (track.sourceManager.sourceName == "pornhub") {
            //ctx.message.channel.sendMessage("**${track.info.title}** added to queue").queue({ m -> m.delete().queueAfter(5, TimeUnit.SECONDS) }, null)
            ctx.embed {
                setAuthor("PornHub is music too",
                        track.info.uri,
                        "https://data.apkhere.com/b2/com.app.pornhub/4.1.1/icon.png!s")
                        .setColor(Colors.getEffectiveColor(ctx.message))
                        .addField("Enqueued track",
                                "**Title**: ${track.info.title}\n" +
                                        "**Duration**: ${Utils.fTime(track.info.length)}\n" +
                                        "**Link**: ${track.info.uri}",
                                false)
                        .setFooter("Requested by ${ctx.author.name}", ctx.author.avatarUrl)
                        .setTimestamp(Instant.now())
                        .build()
            }
        }

        if (track.sourceManager.sourceName == "redtube") {
            //ctx.message.channel.sendMessage("**${track.info.title}** added to queue").queue({ m -> m.delete().queueAfter(5, TimeUnit.SECONDS) }, null)
            ctx.embed {
                setAuthor("RedTube is music too",
                        track.info.uri,
                        "https://cdn.discordapp.com/attachments/440667148315262978/490353839577497623/rt.png")
                        .setColor(Colors.getEffectiveColor(ctx.message))
                        .addField("Enqueued track",
                                "**Title**: ${track.info.title}\n" +
                                        "**Duration**: ${Utils.fTime(track.info.length)}\n" +
                                        "**Link**: ${track.info.uri}",
                                false)
                        .setFooter("Requested by ${ctx.author.name}", ctx.author.avatarUrl)
                        .setTimestamp(Instant.now())
                        .build()

            }

        }
    }

}
