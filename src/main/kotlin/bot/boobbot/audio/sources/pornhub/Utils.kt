package bot.boobbot.audio.sources.pornhub

import bot.boobbot.utils.joinToString
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import java.nio.charset.StandardCharsets


object Utils {
    //private val assignmentPattern = "(var.+?media_0[^<]+)".toPattern()
    //private val assignmentPattern = "(var.+?media_0.+)".toPattern()

    private val jsVarPattern = "(var\\s+(?:media|quality|qualityItems)_.+)".toPattern()
    private val tvMediaStringPattern = "(var.+?mediastring[^<]+)".toPattern()
    private val flashVarRegex = "var flashvars_\\d+ = (\\{.+})".toPattern()
    private val cleanRegex = "/\\*(?:(?!\\*/).)*?\\*/".toRegex()
    private val cleanVarRegex = "var\\s+".toRegex()

    fun extractMediaString(page: String, http: HttpInterface): String {
        val vars = hashMapOf<String, String>()
        val assignments = extractAssignments(page)

        for (assignment in assignments) {
            val trimmed = assignment.trim()

            if (trimmed.isEmpty()) {
                continue
            }

            val noVar = trimmed.replace(cleanVarRegex, "")
            val (name, value) = noVar.split('=', limit = 2)

            vars[name] = parseSegment(value, vars)
        }

        val formats = mutableMapOf<String, String>()

        for ((formatKey, url) in vars) {
            when {
                formatKey.startsWith("quality_") ||
                        formatKey.startsWith("media") -> formats[formatKey] = url

                formatKey.startsWith("flashvars") &&
                        "/video/get_media" in url -> formats.putAll(loadMp4Formats(url, http))

                else -> continue
            }
        }

        return formats["quality_720p"]
            ?: formats["quality_480p"]
            ?: formats["quality_240p"]
            ?: formats["quality_1080p"] // This is last because it's a relatively new option, and might not always be available for free.
            ?: throw IllegalStateException("No formats detected")
    }

    private fun extractAssignments(script: String): List<String> {
        val formats = jsVarPattern.matcher(script)

        if (formats.find()) {
            return formats.group(1).split(';')
        }

        val assignments = tvMediaStringPattern.matcher(script)

        if (!assignments.find()) {
            throw IllegalStateException("No assignments or formats found within the script!")
        }

        return assignments.group(1).split(';')
    }

    private fun loadMp4Formats(getMediaUrl: String, http: HttpInterface): Map<String, String> {
        val formats = mutableMapOf<String, String>()

        http.use {
            it.execute(HttpGet(getMediaUrl)).use { res ->
                val json = JsonBrowser.parse(res.entity.content)

                for (format in json.values()) {
                    if (format.get("format").safeText() != "mp4") {
                        continue
                    }

                    val quality = format.get("quality").text() + 'p' // 240, 480, 720, 1080
                    val videoUrl = format.get("videoUrl").text()
                    formats["quality_$quality"] = videoUrl
                }
            }
        }

        return formats
    }

    private fun parseSegment(segment: String, v: HashMap<String, String>): String {
        val cleaned = segment.replace(cleanRegex, "").trim()

        if (cleaned.contains('+')) {
            val subSegments = cleaned.split('+')
            return subSegments.joinToString("") { parseSegment(it, v) }
        }

        return v[cleaned]
            ?: cleaned.replace("'", "").replace("\"", "")
    }
}
