package bot.boobbot.audio

import bot.boobbot.BoobBot
import bot.boobbot.flight.Context
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class AudioLoader(private val musicManager: GuildMusicManager, private val ctx: Context) : AudioLoadResultHandler {

    override fun trackLoaded(track: AudioTrack) {
        enqueueTrack(track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        if (playlist.isSearchResult) {
            enqueueTrack(playlist.tracks[0])
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

        ctx.send("**${track.info.title}** added to queue")
    }

}