package bot.boobbot.audio.sources.pornhub

import com.sedmelluq.discord.lavaplayer.container.mpeg.MpegAudioTrack
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import java.io.IOException
import java.net.URI
import java.nio.charset.StandardCharsets


class PornHubAudioTrack(trackInfo: AudioTrackInfo, private val sourceManager: PornHubAudioSourceManager) :
    DelegatedAudioTrack(trackInfo) {

    override fun makeClone() = PornHubAudioTrack(trackInfo, sourceManager)

    override fun getSourceManager() = sourceManager

    @Throws(Exception::class)
    override fun process(localExecutor: LocalAudioTrackExecutor) {
        sourceManager.httpInterfaceManager.`interface`.use {
            processStatic(localExecutor, it)
        }
    }

    @Throws(Exception::class)
    private fun processStatic(localExecutor: LocalAudioTrackExecutor, httpInterface: HttpInterface) {
        val playbackUrl = getPlaybackUrl(httpInterface)

        PersistentHttpStream(httpInterface, URI(playbackUrl), Long.MAX_VALUE).use { stream ->
            processDelegate(MpegAudioTrack(trackInfo, stream), localExecutor)
        }
    }

    private fun getPlaybackUrl(httpInterface: HttpInterface): String {
        httpInterface.execute(HttpGet(trackInfo.uri)).use { response ->
            val statusCode = response.statusLine.statusCode

            if (!HttpClientTools.isSuccessWithContent(statusCode)) {
                throw IOException("Invalid status code for response: $statusCode")
            }

            return Utils.extractMediaString(EntityUtils.toString(response.entity, StandardCharsets.UTF_8), httpInterface)
        }
    }
}
