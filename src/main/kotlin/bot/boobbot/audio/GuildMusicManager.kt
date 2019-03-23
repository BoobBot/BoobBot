package bot.boobbot.audio

import bot.boobbot.BoobBot
import com.github.natanbc.catnipvoice.AudioProvider
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import com.github.natanbc.catnipvoice.ExampleBot
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import java.nio.ByteBuffer
import org.xnio.Buffers.position
import org.springframework.core.convert.TypeDescriptor.array
import com.sedmelluq.discord.lavaplayer.track.playback.ImmutableAudioFrame
import javax.annotation.Nonnull




class GuildMusicManager(val guildId: String, val player: AudioPlayer) : AudioEventAdapter(), AudioProvider {

    private val buffer = ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())
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
    // JDA SEND HANDLER HOOKS
    // ----------------------------------------------

//    override fun provide20MsAudio(): ByteArray {
//        return lastFrame!!.data
//        // We know this won't be null here as JDA will only call this if canProvide is true
//        // And we already do null checks in canProvide
//    }
//
//    override fun canProvide(): Boolean {
//        lastFrame = player.provide()
//        return lastFrame != null
//    }
//
//    override fun isOpus(): Boolean = true


    // ----------------------------------------------
    // catnip SEND HANDLER HOOKS
    // ----------------------------------------------

    override fun canProvide(): Boolean {
        lastFrame = player.provide()
       return lastFrame != null
    }

    override fun provide(): ByteBuffer {
        if (lastFrame is ImmutableAudioFrame) {
            lastFrame!!.getData(buffer.array(), lastFrame!!.dataLength)
        } else {
            lastFrame!!.getData(buffer.array(), 0)
        }
        return buffer.position(0).limit(lastFrame!!.dataLength)
    }

    override fun isOpus(): Boolean = true


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
