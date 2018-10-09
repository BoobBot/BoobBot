package bot.boobbot.audio

import bot.boobbot.BoobBot
import bot.boobbot.flight.Context
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.concurrent.TimeUnit
import java.util.Random



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
        track.userData = ctx.guild
        musicManager.addToQueue(track)
       if (track.sourceManager.sourceName != "local") {
           ctx.message.channel.sendMessage("**${track.info.title}** added to queue").queue()//{ m -> m.delete().queueAfter(5, TimeUnit.SECONDS) }, null)
       }
    }

}