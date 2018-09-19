package bot.boobbot.misc

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import net.dv8tion.jda.core.entities.User
import okhttp3.Headers
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO


class Utils {
    companion object {
        fun isDonor(user: User): Boolean {
            val member = BoobBot.home?.getMember(user) ?: return false
            return member.roles.any { r -> r.idLong == 440542799658483713L }
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
