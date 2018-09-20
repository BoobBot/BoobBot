package bot.boobbot.misc

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import com.google.common.collect.Lists
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.entities.User
import okhttp3.Headers
import org.json.JSONObject
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.text.MessageFormat
import java.time.Instant.now
import java.util.*
import javax.imageio.ImageIO


class Utils {
    companion object {
        fun isDonor(user: User): Boolean {
            val member = BoobBot.home?.getMember(user) ?: return false
            return member.roles.any { r -> r.idLong == 440542799658483713L }
        }

        fun getRandomFunString(key: String): String {
            val jsonObj = JSONObject(File(BoobBot::class.java.classLoader.getResource("fun.json").file).bufferedReader().use { it.readText() })
            return jsonObj.getJSONArray(key).get(Random().nextInt(jsonObj.getJSONArray(key).length())).toString()
        }

        fun getProxy(): Proxy {
            val ps = Lists.newArrayList(
                    "5.231.237.168:3213",
                    "94.249.224.97:2543",
                    "185.164.57.91:4012",
                    "185.164.57.144:9749",
                    "185.164.57.70:5756")
            val rand = Random()
            val proxy = ps[rand.nextInt(ps.size)]
            val parts = proxy.split(":".toRegex(), 2).toTypedArray()
            return Proxy(Proxy.Type.HTTP, InetSocketAddress(parts[0], Integer.parseInt(parts[1])))
        }


        fun logCommand(message: net.dv8tion.jda.core.entities.Message) =
                if ((message.isFromType(ChannelType.PRIVATE))) {
                    val msg = MessageFormat.format(
                            "{4}: {0} Used {1} on Channel: {2}({3})",
                            message.author.name,
                            message.contentRaw,
                            message.channel.name,
                            message.channel.id,
                            now())
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
                            now())
                    BoobBot.log.info(msg)
                }


        fun getCommand(commandName: String): Command? {
            val commands = BoobBot.getCommands()
            return commands[commandName]
                    ?: commands.values.firstOrNull { it.properties.aliases.contains(commandName) }

        }

        fun downloadAvatar(url: String): BufferedImage? {
            val body = BoobBot.requestUtil.get(url, Headers.of()).block()?.body()
                    ?: return null

            return try {
                ImageIO.read(body.byteStream())
            } catch (e: IOException) {
                null
            }
        }
    }
}
