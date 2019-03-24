package bot.boobbot.audio

import bot.boobbot.BoobBot
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.core.audio.AudioSendHandler

class GuildMusicManager(val guildId: String, val player: AudioPlayer) : AudioEventAdapter(), AudioSendHandler {

    private var lastFrame: AudioFrame? = null
    val queue = mutableListOf<AudioTrack>()
    private var lastTrack: AudioTrack? = null
    private var repeat = RepeatMode.NONE


    init {
        player.addListener(this)
    }

    // ----------------------------------------------
    // CUSTOM FUNCTIONS
    // ----------------------------------------------

    public fun addToQueue(track: AudioTrack) {
        if (!player.startTrack(track, true)) {
            queue.add(track)
        }
    }

    public fun playNext() {
        if (queue.size > 0) {
            player.startTrack(queue.removeAt(0), false)
        } else {
            shutdown()
        }
    }

    // ----------------------------------------------
    // PLAYER EVENT HANDLING
    // ----------------------------------------------

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        lastTrack = track
        if (!endReason.mayStartNext) {
            return
        }

        val cloned = track.makeClone()
        cloned.userData = track.userData

        when (repeat) {
            RepeatMode.SINGLE -> player.startTrack(cloned, false)
            RepeatMode.ALL -> {
                queue.add(cloned)
                playNext()
            }
            RepeatMode.NONE -> {
                playNext()
            }
        }
    }

    // ----------------------------------------------
    // catnip SEND HANDLER HOOKS
    // ----------------------------------------------

    override fun canProvide(): Boolean {
        lastFrame = player.provide()
        return lastFrame != null
    }

    override fun provide20MsAudio(): ByteArray {
        return lastFrame!!.data
    }

    override fun isOpus() = true


    // ----------------------------------------------
    // MISC
    // ----------------------------------------------

    public enum class RepeatMode {
        SINGLE, ALL, NONE
    }

    public fun shutdown() {
        player.stopTrack()
        player.destroy()

//        val guild = BoobBot.shardManager.getGuildById(guildId)
//
//        if (guild != null) {
//            guild.audioManager.sendingHandler = null
//            disconnect()
//        }

        BoobBot.musicManagers.remove(guildId)
    }

    public fun disconnect() {
        //BoobBot.shardManager.getGuildById(guildId)?.audioManager?.closeAudioConnection()
    }
}
