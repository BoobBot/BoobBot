package bot.boobbot.misc

import bot.boobbot.BoobBot
import bot.boobbot.flight.Context
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

class AudioLoader(private val musicManager: GuildMusicManager, private val ctx: Context) : AudioLoadResultHandler {


    override fun trackLoaded(track: AudioTrack) {
        track.userData = ctx.guild
        musicManager.addToQueue(track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
    }

    override fun noMatches() {
        //todo handle this when full audio added
        }


    override fun loadFailed(e: FriendlyException) {
        BoobBot.log.error("wot", e) //should not happen rn all local tracks
    }


}