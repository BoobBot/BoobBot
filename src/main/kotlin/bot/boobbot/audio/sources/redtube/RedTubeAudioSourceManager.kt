package bot.boobbot.audio.sources.redtube

import bot.boobbot.misc.Utils
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager
import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioReference
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import org.apache.commons.io.IOUtils
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import java.nio.charset.Charset
import java.util.function.Consumer
import java.util.function.Function
import java.util.regex.Pattern

class RedTubeAudioSourceManager : AudioSourceManager, HttpConfigurable {
    private val httpInterfaceManager: HttpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager()

    val httpInterface: HttpInterface
        get() = httpInterfaceManager.`interface`

    override fun getSourceName(): String {
        return "redtube"
    }

    override fun loadItem(manager: DefaultAudioPlayerManager, reference: AudioReference): AudioItem? {
        if (!videoUrlPattern.matcher(reference.identifier).matches()) return null

        return try {
            loadItemOnce(reference)
        } catch (exception: FriendlyException) {
            if (HttpClientTools.isRetriableNetworkException(exception.cause)) {
                loadItemOnce(reference)
            } else {
                throw exception
            }
        }

    }

    override fun isTrackEncodable(track: AudioTrack) = true

    override fun encodeTrack(track: AudioTrack, output: DataOutput) {

    }

    override fun decodeTrack(trackInfo: AudioTrackInfo, input: DataInput): AudioTrack {
        return RedTubeAudioTrack(trackInfo, this)
    }

    override fun shutdown() = Utils.suppressExceptions {
        httpInterface.close()
    }

    override fun configureRequests(configurator: Function<RequestConfig, RequestConfig>) {
        httpInterfaceManager.configureRequests(configurator)
    }

    override fun configureBuilder(configurator: Consumer<HttpClientBuilder>) {
        httpInterfaceManager.configureBuilder(configurator)
    }

    private fun loadItemOnce(reference: AudioReference): AudioItem {
        httpInterface.use { httpInterface ->
            val info = getVideoInfo(httpInterface, reference.identifier) ?: return AudioReference.NO_TRACK

            var playbackURL: String? = null

            for (format in info.get("mediaDefinitions").values()) {
                if (!format.get("videoUrl").text().isEmpty()) {
                    playbackURL = format.get("videoUrl").text()
                    break
                }
            }

            if (playbackURL == null) {
                return AudioReference.NO_TRACK
            }

            val videoTitle = info.get("video_title").text()
            val videoDuration = Integer.parseInt(info.get("video_duration").text()) * 1000

            return buildTrackObject(
                reference.identifier, playbackURL, videoTitle, "Unknown Uploader", false, videoDuration.toLong()
            )
        }
    }

    @Throws(IOException::class)
    fun getVideoInfo(httpInterface: HttpInterface, videoURL: String): JsonBrowser? {
        httpInterface.execute(HttpGet(videoURL)).use { response ->
            val statusCode = response.statusLine.statusCode
            if (statusCode != 200) {
                throw IOException("Invalid status code for video page response: $statusCode")
            }

            val html = IOUtils.toString(response.entity.content, CHARSET)
            val match = VIDEO_INFO_REGEX.matcher(html)

            return if (match.find()) {
                JsonBrowser.parse(match.group(1))
            } else {
                null
            }
        }
    }

    private fun buildTrackObject(
        uri: String,
        identifier: String,
        title: String,
        uploader: String,
        isStream: Boolean,
        duration: Long
    ): RedTubeAudioTrack {
        return RedTubeAudioTrack(
            AudioTrackInfo(title, uploader, duration, identifier, isStream, uri), this
        )
    }

    companion object {
        private val CHARSET = Charset.forName("UTF-8")
        private const val VIDEO_REGEX = "^https?://www.redtube.com/\\d{4,8}$"
        private val VIDEO_INFO_REGEX = Pattern.compile("playervars: (\\{.+})")
        private val videoUrlPattern = Pattern.compile(VIDEO_REGEX)
    }
}
