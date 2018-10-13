package bot.boobbot.audio.sources.pornhub

import com.sedmelluq.discord.lavaplayer.container.mpeg.MpegAudioTrack
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import java.net.URI
import java.nio.charset.Charset
import java.util.regex.Pattern

class PornHubAudioTrack(trackInfo: AudioTrackInfo, private val sourceManager: PornHubAudioSourceManager) : DelegatedAudioTrack(trackInfo) {

    override fun makeClone(): AudioTrack {
        return PornHubAudioTrack(trackInfo, sourceManager)
    }

    override fun getSourceManager(): AudioSourceManager {
        return sourceManager
    }

    @Throws(Exception::class)
    override fun process(localExecutor: LocalAudioTrackExecutor) {
        sourceManager.httpInterfaceManager.`interface`.use {
            processStatic(localExecutor, it)
        }
    }

    @Throws(Exception::class)
    private fun processStatic(localExecutor: LocalAudioTrackExecutor, httpInterface: HttpInterface) {
        val playbackUrl = getPlaybackUrl(httpInterface) ?: throw Exception("no playback url found")

        PersistentHttpStream(httpInterface, URI(playbackUrl), Long.MAX_VALUE).use { stream ->
            processDelegate(MpegAudioTrack(trackInfo, stream), localExecutor)
        }
    }

    private fun getPlaybackUrl(httpInterface: HttpInterface): String? {
        val info = getPageConfig(httpInterface)
                ?: throw FriendlyException("This track is unplayable", FriendlyException.Severity.SUSPICIOUS, null)

        return info.get("mediaDefinitions").values().stream()
                .filter { format -> format.get("videoUrl").text().isNotEmpty() }
                .findFirst()
                .orElse(null)!!.get("videoUrl").text() ?: null
    }

    private fun getPageConfig(httpInterface: HttpInterface): JsonBrowser? {
        httpInterface.execute(HttpGet(trackInfo.uri)).use { response ->
            val statusCode = response.statusLine.statusCode

            if (statusCode != 200) {
                return null
            }

            val html = IOUtils.toString(response.entity.content, Charset.forName(CHARSET))
            val match = VIDEO_INFO_REGEX.matcher(html)

            return if (match.find()) JsonBrowser.parse(match.group(1)) else null
        }
    }

    companion object {
        private val VIDEO_INFO_REGEX = Pattern.compile("var flashvars_\\d{7,9} = (\\{.+})")
        private const val CHARSET = "UTF-8"
    }

}
