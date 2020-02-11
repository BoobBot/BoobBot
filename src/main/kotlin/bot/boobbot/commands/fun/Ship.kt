package bot.boobbot.commands.`fun`

import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.math.max


private fun mixString(a: String, b: String): String? {
    val max = max(a.length, b.length)
    val mixed = StringBuilder()
    for (i in 0 until max) {
        if (i <= a.length - 1) mixed.append(a, i, i + 1)
        if (i <= b.length - 1) mixed.append(b, i, i + 1)
    }
    return mixed.toString()
}

private fun getAvatar(user: User): BufferedImage? {
    var ava: BufferedImage? = null
    try {
        val userAva = URL(user.effectiveAvatarUrl + "?size=160")
        val connection = userAva.openConnection()
        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"
        )
        connection.connect()
        ava = ImageIO.read(connection.getInputStream())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ava
}


@CommandProperties(description = "Shipped", category = Category.FUN)
class Ship : Command {
    override fun execute(ctx: Context) {
        ctx.channel.sendTyping().queue()
        val user: User
        val user1: User
        if (ctx.message.mentionedUsers.isEmpty()) {
            return ctx.send("How in the fuck would i know who you want to ship if you don't give me a valid target?")
        } else {
            user1 = if (ctx.message.mentionedUsers.size > 1) {
                ctx.message.mentionedUsers[1]
            } else {
                ctx.message.author
            }
            user = ctx.message.mentionedUsers[0]
            if (user == ctx.selfUser) {
                return ctx.send("Don't you fucking touch me whore, I will end you.")
            }
            if (user == ctx.author) {
                return ctx.send("You must be special if you're really trying to ship yourself.")
            }
            val target1 = getAvatar(user)
            val target = getAvatar(user1)
            try {
                val rng = (0..100).random()
                val template = ImageIO.read(this.javaClass.getResource("/boobLove.png"))
                val bg = BufferedImage(template.width, template.height, template.type)
                val image = bg.createGraphics()
                val font = Font("Whitney", Font.BOLD, 36)
                image.color = Color(51, 232, 211)
                image.font = font
                image.drawImage(target, 0, 0, 160, 160, null)
                image.drawImage(target1, 320, 0, 160, 160, null)
                image.drawImage(template, 0, 0, null)
                when (rng) {
                    100 -> {
                        image.drawString(String.format("%s", rng), 207, 157)
                    }
                    in 0..9 -> {
                        image.drawString(String.format("%s", rng), 250, 157)
                    }
                    else -> {
                        image.drawString(String.format("%s", rng), 222, 157)
                    }
                }
                image.dispose()
                val stream = ByteArrayOutputStream()
                ImageIO.setUseCache(false)
                ImageIO.write(bg, "png", stream)
                val msg: Message = MessageBuilder()
                    .append(mixString(user.name, user1.name))
                    .append(" ")
                    .append("<:icon:676613489548197915>")
                    .build()
                ctx.channel.sendMessage(msg).addFile(stream.toByteArray(), "shipped.png").queue()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
