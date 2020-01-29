package bot.boobbot.audio.sources.pornhub

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.ExceptionTools
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.FAULT
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable
import com.sedmelluq.discord.lavaplayer.track.*
import org.apache.commons.io.IOUtils
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClientBuilder
import org.jsoup.Jsoup
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.regex.Pattern


class PornHubAudioSourceManager : AudioSourceManager, HttpConfigurable {
    val httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager()!!

    override fun getSourceName() = "pornhub"

    override fun loadItem(manager: DefaultAudioPlayerManager, reference: AudioReference): AudioItem? {
        if (!VIDEO_REGEX.matcher(reference.identifier).matches() && !reference.identifier.startsWith(VIDEO_SEARCH_PREFIX))
            return null

        if (reference.identifier.startsWith(VIDEO_SEARCH_PREFIX)) {
            return searchForVideos(reference.identifier.substring(VIDEO_SEARCH_PREFIX.length).trim())
        }

        return try {
            loadItemOnce(reference)
        } catch (exception: FriendlyException) {
            // In case of a connection reset exception, try once more.
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

    override fun decodeTrack(trackInfo: AudioTrackInfo, input: DataInput) = PornHubAudioTrack(trackInfo, this)

    override fun shutdown() {
        httpInterfaceManager.close()
    }

    override fun configureRequests(configurator: Function<RequestConfig, RequestConfig>) {
        httpInterfaceManager.configureRequests(configurator)
    }

    override fun configureBuilder(configurator: Consumer<HttpClientBuilder>) {
        httpInterfaceManager.configureBuilder(configurator)
    }

    private fun loadItemOnce(reference: AudioReference): AudioItem {
        try {
            val info = getVideoInfo(reference.identifier) ?: return AudioReference.NO_TRACK

            if (info.get("video_unavailable").text() == "true")
                return AudioReference.NO_TRACK

            val videoTitle = info.get("video_title").text()
            val videoDuration = info.get("video_duration").text().toInt() * 1000 // PH returns seconds
            val videoUrl = info.get("link_url").text()
            val matcher = VIDEO_REGEX.matcher(videoUrl)
            val videoId = if (matcher.matches()) matcher.group(1) else reference.identifier

            return buildTrackObject(videoUrl, videoId, videoTitle, "Unknown Uploader", false, videoDuration.toLong())
        } catch (e: Exception) {
            throw ExceptionTools.wrapUnfriendlyExceptions("Loading information for a PornHub track failed.", FAULT, e)
        }
    }

    private fun searchForVideos(query: String): AudioItem {
        val uri = URIBuilder("https://www.pornhub.com/video/search")
            .addParameter("search", query)
            .build()

        makeHttpRequest(HttpGet(uri)).use {
            val statusCode = it.statusLine.statusCode

            if (statusCode != 200) {
                if (statusCode == 404) {
                    return AudioReference.NO_TRACK
                }
                throw IOException("Invalid status code for search response: $statusCode")
            }

            val document = Jsoup.parse(it.entity.content, StandardCharsets.UTF_8.name(), "https://pornhub.com")
            val videos = document.getElementsByClass("wrap")
                .filter { elem ->
                    !elem.select("div.thumbnail-info-wrapper span.title a")
                        .first()
                        .attr("href")
                        .contains("playlist")
                }

            if (videos.isEmpty()) {
                return AudioReference.NO_TRACK
            }

            val tracks = ArrayList<AudioTrack>()

            for (e in videos) {
                val anchor = e.select("div.thumbnail-info-wrapper span.title a").first()
                val title = anchor.text()
                val identifier = anchor.parents().select("li.videoBox").first().attr("_vkey")
                val url = anchor.absUrl("href")
                val durationStr =
                    anchor.parents().select("div.videoPreviewBg .marker-overlays var").firstOrNull()?.text()
                val duration = if (durationStr != null) parseDuration(durationStr) else 0L

                tracks.add(buildTrackObject(url, identifier, title, "Unknown Uploader", false, duration))
            }

            return BasicAudioPlaylist("Search results for: $query", tracks, null, true)
        }
    }

    @Throws(IOException::class)
    private fun getVideoInfo(videoURL: String): JsonBrowser? {
        makeHttpRequest(HttpGet(videoURL)).use {
            val statusCode = it.statusLine.statusCode

            if (statusCode != 200) {
                if (statusCode == 404) {
                    return null
                }
                throw IOException("Invalid status code for video page response: $statusCode")
            }

            val html = IOUtils.toString(it.entity.content, StandardCharsets.UTF_8)
            val match = VIDEO_INFO_REGEX.matcher(html)

            return if (match.find()) JsonBrowser.parse(match.group(1)) else null
        }
    }

    private fun buildTrackObject(
        uri: String,
        identifier: String,
        title: String,
        uploader: String,
        isStream: Boolean,
        duration: Long
    ): PornHubAudioTrack {
        return PornHubAudioTrack(
            AudioTrackInfo(title, uploader, duration, identifier, isStream, uri),
            this
        )
    }

    private fun parseDuration(duration: String): Long {
        val time = duration.split(":")
        val mins = time[0].toLong() * 60000L
        val secs = time[1].toLong() * 1000L

        return mins + secs
    }

    private fun makeHttpRequest(request: HttpUriRequest) = httpInterfaceManager.`interface`.use {
        it.execute(request)
    }

    companion object {
        private val VIDEO_REGEX =
            Pattern.compile("^https?://www\\.pornhub\\.com/view_video\\.php\\?viewkey=([a-zA-Z0-9]{9,15})\$")
        private val VIDEO_INFO_REGEX = Pattern.compile("var flashvars_\\d{7,9} = (\\{.+})")
        private const val VIDEO_SEARCH_PREFIX = "phsearch:"
    }

}
