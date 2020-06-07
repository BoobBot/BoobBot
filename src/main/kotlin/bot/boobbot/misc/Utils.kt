package bot.boobbot.misc

import bot.boobbot.BoobBot
import net.dv8tion.jda.api.entities.Message
import okhttp3.Headers
import org.apache.commons.io.IOUtils
import org.apache.http.HttpHost
import org.json.JSONObject
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Proxy
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import javax.imageio.ImageIO


class Utils {
    companion object {
        private val rand = Random()
        private val path = Paths.get("").toAbsolutePath().toString()
        private val ips = listOf(
            "104.247.201.235:8564", //Atlanta
            "104.247.211.200:4041", //PeachtreeCity
            "104.237.210.97:4974", //Chicago
            "172.82.172.126:1248", //Schererville
            "45.43.216.46:3568",  //Los Angeles
            "45.43.217.133:8058", //Los Angeles
            "45.58.59.214:6560", //Atlanta
            "96.46.0.53:1520", //Duarte
            "208.72.224.182:6958", //Duarte
            "67.227.66.102:7498" // Las Vegas
        )

        val version by lazy {
            val stream = BoobBot::class.java.classLoader.getResourceAsStream("version.txt")!!
            return@lazy InputStreamReader(stream).readText()
        }

        private val jsonArrays = JSONObject(
            if (File("$path/arrays.json").exists()) {
                (File("$path/arrays.json").inputStream())
                    .bufferedReader()
                    .use { it.readText() }
            } else {
                (BoobBot::class.java.classLoader.getResourceAsStream("arrays.json"))!!
                    .bufferedReader()
                    .use { it.readText() }
            }
        )


        fun checkDonor(event: Message): Boolean {
            return BoobBot.pApi.getDonorType(event.author.id).tier >= 1 // Supporter, Server Owner, Developer
            || (event.channelType.isGuild && BoobBot.pApi.getDonorType(event.guild.ownerId) == DonorType.SERVER_OWNER)
        }

        fun getRandomFunString(key: String): String {
            val arr = jsonArrays.getJSONArray(key)
            return arr.getString(rand.nextInt(arr.length()))
        }

        fun getRandomMoan(): File {
            val arr = jsonArrays.getJSONArray("moan")
            val fileOjb = arr.getJSONObject(rand.nextInt(arr.length()))
            return File("$path/moan/${fileOjb.get("name")}.${fileOjb.get("ext")}")
        }

        fun getProxy(): Proxy {
            val proxy = ips[rand.nextInt(ips.size)]
            val parts = proxy.split(":".toRegex(), 2).toTypedArray()
            return Proxy(Proxy.Type.HTTP, InetSocketAddress(parts[0], parts[1].toInt()))
        }

        @Suppress("unused")
        fun getProxyAsHost(): HttpHost {
            val proxy = ips[rand.nextInt(ips.size)]
            val parts = proxy.split(":".toRegex(), 2).toTypedArray()
            return HttpHost(parts[0], parts[1].toInt(), "http")

        }

        fun connectToVoiceChannel(message: Message) {
            if (!message.guild.audioManager.isConnected && !message.guild.audioManager.isAttemptingToConnect) {
                message.guild.audioManager.openAudioConnection(message.member!!.voiceState!!.channel)
            }
        }

        fun logCommand(m: Message) {
            val msg = if (m.isFromGuild) {
                "[${m.guild.name} (${m.guild.id})/${m.channel.name} (${m.channel.id})] ${m.author.asTag} (${m.author.id}): ${m.contentDisplay}"
            } else {
                "[DM/${m.channel.name} (${m.channel.id})] ${m.author.asTag} (${m.author.id}): ${m.contentDisplay}"
            }

            BoobBot.log.info(msg)
        }

        fun fTime(milliseconds: Long): String {
            val seconds = milliseconds / 1000 % 60
            val minutes = milliseconds / (1000 * 60) % 60
            val hours = milliseconds / (1000 * 60 * 60) % 24
            val days = milliseconds / (1000 * 60 * 60 * 24)

            return when {
                days > 0 -> String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
                hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
                else -> String.format("%02d:%02d", minutes, seconds)
            }
        }

        fun readAll(inputStream: InputStream): String = IOUtils.toString(inputStream, Charsets.UTF_8)
    }
}


