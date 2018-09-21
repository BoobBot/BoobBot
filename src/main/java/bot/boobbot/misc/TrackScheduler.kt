package bot.boobbot.misc

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import net.dv8tion.jda.core.entities.User

import java.util.LinkedList
import java.util.Queue

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
class TrackScheduler
/**
 * @param player The audio player this scheduler uses
 */
(val player: AudioPlayer) : AudioEventAdapter() {
    val queue: Queue<AudioTrack>
    var lastTrack: AudioTrack? = null
    var isLoop = false
    var isLoopall = false

    init {
        this.queue = LinkedList()
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    fun queue(track: AudioTrack) {

        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is
        // currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already
        // playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track)
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    fun nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was
        // empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false)
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        this.lastTrack = track
        val user = track!!.userData as User
        if (endReason!!.mayStartNext) {
            val ltrack = track.makeClone()
            ltrack.userData = user
            if (isLoop) {
                player!!.startTrack(ltrack, false)
                return
            }
            if (isLoopall) {
                queue.add(ltrack)
                nextTrack()
            } else {
                nextTrack()
            }
        }
    }

    override fun onTrackStuck(player: AudioPlayer?, track: AudioTrack?, thresholdMs: Long) {
        nextTrack()
    }
}
