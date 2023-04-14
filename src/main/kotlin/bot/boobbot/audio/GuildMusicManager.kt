package bot.boobbot.audio


import bot.boobbot.BoobBot
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class GuildMusicManager(val guildId: Long, val player: AudioPlayer) : AudioEventAdapter(), AudioSendHandler {
    private var shutdown = false
    val queue = mutableListOf<AudioTrack>()

    private var lastTrack: AudioTrack? = null
    private var repeat = RepeatMode.NONE


    init {
        player.addListener(this)
    }

    // ----------------------------------------------
    // CUSTOM FUNCTIONS
    // ----------------------------------------------

    fun addToQueue(track: AudioTrack) {
        if (!player.startTrack(track, true)) {
            queue.add(track)
        }
    }

    fun playNext() {
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

    private val frameBuffer = ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())
    private val lastFrame = MutableAudioFrame().also { it.setBuffer(frameBuffer) }

    override fun provide20MsAudio() = frameBuffer.flip()
    override fun canProvide() = player.provide(lastFrame)
    override fun isOpus() = true


    // ----------------------------------------------
    // MISC
    // ----------------------------------------------

    @Synchronized
    fun shutdown() {
        if (shutdown) {
            return
        }

        player.stopTrack()
        player.destroy()

        BoobBot.shardManager.getGuildById(guildId)?.let {
            it.audioManager.sendingHandler = null
            it.audioManager.closeAudioConnection()
        }

        BoobBot.musicManagers.remove(guildId)
        shutdown = true
    }

    companion object {
        enum class RepeatMode {
            SINGLE, ALL, NONE
        }
    }
}