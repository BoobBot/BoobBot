package bot.boobbot.misc

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.User
import okhttp3.Headers
import okhttp3.Response

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.IOException
import java.util.Arrays
import java.net.InetSocketAddress
import org.json.HTTP
import java.util.Random
import com.google.common.collect.Lists
import java.net.Proxy


object Utils {

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


    fun isDonor(user: User): Boolean {
        val member = user.jda.asBot().shardManager.getGuildById(Constants.HOME_GUILD).getMember(user)
        return member?.roles?.parallelStream()?.anyMatch { x -> x.idLong == 440542799658483713L } ?: false
    }


    fun getCommand(commandName: String): Command? {
        val commands = BoobBot.getCommands()

        return if (commands.containsKey(commandName)) {
            commands[commandName]
        } else commands.values
                .stream()
                .filter { c -> Arrays.asList<String>(*c.properties.aliases).contains(commandName) }
                .findFirst()
                .orElse(null)

    }

    fun downloadAvatar(url: String): BufferedImage? {
        val res = BoobBot.getRequestUtil().get(url, Headers.of()).block()

        if (res?.body() == null) {
            return null
        }

        return try {
            ImageIO.read(res.body()!!.byteStream())
        } catch (e: IOException) {
            null
        }

    }

}
