package bot.boobbot.misc

import bot.boobbot.BoobBot
import bot.boobbot.BoobBot.Companion.manSetAvatar
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Icon
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import okhttp3.Headers
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
import java.text.MessageFormat
import java.time.Instant.now
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

        private val jsonArrays = JSONObject(
            if (File("$path/arrays.json").exists()) {
                (File("$path/arrays.json").inputStream())
                    .bufferedReader()
                    .use { it.readText() }
            } else {
                (BoobBot::class.java.classLoader.getResourceAsStream("arrays.json"))
                    .bufferedReader()
                    .use { it.readText() }
            }
        )

        /** LEGACY */
        fun hasRole(user: User, roleId: Long): Boolean {
            return BoobBot.shardManager.home?.getMember(user)?.roles?.any { it.idLong == roleId } ?: false
        }

        fun isStreamer(user: User) = hasRole(user, 618266918754713610L)
        fun isBooster(user: User) = hasRole(user, 585533009797578759L)
        fun isDonor(user: User) = hasRole(user, 528615837305929748L)
        fun isDonorPlus(user: User) = hasRole(user, 528615882709008430L)

        fun checkDonor(event: Message): Boolean {
            val legacyChecks =
                isDonor(event.author) || (event.channelType.isGuild && isDonorPlus(event.guild.owner!!.user))

            // getDonorType automatically checks developer status.
            return legacyChecks
                    || BoobBot.pApi.getDonorType(event.author.id).tier >= 1 // Supporter, Server Owner, Developer
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

        private fun getRandomAvatar(): InputStream {
            val arr = jsonArrays.getJSONArray("avatar")
            val fileOjb = arr.getJSONObject(rand.nextInt(arr.length()))
            val file = File("$path/avatar/${fileOjb.get("name")}.${fileOjb.get("ext")}")
            return if (file.exists()) {
                file.inputStream()
            } else {
                BoobBot::class.java.classLoader
                    .getResourceAsStream("avatar/${fileOjb.get("name")}.${fileOjb.get("ext")}")!!
            }
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


        fun logCommand(message: Message) =
            if (message.channelType == ChannelType.PRIVATE) {
                val msg = MessageFormat.format(
                    "{3}: {0} Used {1} in DM ({2})",
                    message.author.name,
                    message.contentRaw,
                    message.channel.id,
                    now()
                )
                BoobBot.log.info(msg)
            } else {
                val msg = MessageFormat.format(
                    "{6}: {0} Used {1} on Guild:{4}({5}) in Channel: {2}({3})",
                    message.author.name,
                    message.contentRaw,
                    message.channel.name,
                    message.channel.id,
                    message.guild.name,
                    message.guild.id,
                    now()
                )
                BoobBot.log.info(msg)
            }

        fun downloadAvatar(url: String): BufferedImage? {
            val body = BoobBot.requestUtil.get(url, Headers.of()).block()?.body()
                ?: return null

            var image: BufferedImage? = null

            body.byteStream().use {
                image = ImageIO.read(it)
            }

            return image
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

        fun readAll(inputStream: InputStream): String {
            return BufferedReader(
                InputStreamReader(inputStream)
            ).lines().collect(Collectors.joining("\n"))
        }

        private fun autoAvatar() {
            if (!manSetAvatar) {
                val icon = Icon.from(getRandomAvatar())
                BoobBot.shardManager.home?.manager?.setIcon(icon)?.queue()
                BoobBot.shardManager.shards[0].selfUser.manager.setAvatar(icon).queue()
                BoobBot.log.info("Setting New Guild icon/Avatar")
            }
        }

        fun auto() = Runnable { autoAvatar() }

        inline fun suppressExceptions(block: () -> Unit) = try {
            block()
        } catch (e: Exception) {
        }

        fun updateStats() {
            BoobBot.guilds = BoobBot.shardManager.guilds.size.toString()
            BoobBot.users = BoobBot.shardManager.users.size.toString()
        }
    }
}


