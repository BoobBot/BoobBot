package bot.boobbot.commands.`fun`

import bot.boobbot.BoobBot
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
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import kotlin.math.max


@CommandProperties(description = "Shipped", category = Category.FUN)
class Ship : Command {
    override fun execute(ctx: Context) {
        if (ctx.mentions.isEmpty()) {
            return ctx.send("How in the fuck would i know who you want to ship if you don't give me a valid target?")
        }

        val user1 = ctx.mentions.first()
        val user2 = ctx.mentions.lastOrNull() ?: ctx.author

        if (user1.idLong == ctx.author.idLong && user2.idLong == ctx.author.idLong) {
            return ctx.send("not even you should wanna be shipped with yourself")
        }

        if (user1.idLong == user2.idLong) {
            return ctx.send("how the fuck are you going to ship the same person whore")
        }

        if (user1.idLong == ctx.selfUser.idLong || user2.idLong == ctx.selfUser.idLong) {
            return ctx.send("Don't you fucking touch me whore, I will end you.")
        }

        ctx.channel.sendTyping().queue()

        val av1Fut = downloadAvatar(user1.effectiveAvatarUrl)
        val av2Fut = downloadAvatar(user2.effectiveAvatarUrl)

        av1Fut.thenCombine(av2Fut) { av1, av2 ->
            val result = processImages(av1, av2)
            val content = MessageBuilder()
                .setContent(mixString(user1.name, user2.name))
                .append(" <:icon:676613489548197915>")
                .build()

            ctx.channel.sendMessage(content)
                .addFile(result.toByteArray(), "shipped.png")
                .submit()
                .whenComplete { m, e -> result.close() }
        }
    }

    companion object {
        fun downloadAvatar(url: String): CompletableFuture<BufferedImage> {
            return BoobBot.requestUtil.get(url).submit()
                .thenApply { it.body()?.byteStream() ?: throw IllegalStateException("ResponseBody is null!") }
                .thenApply { ImageIO.read(it) }
        }

        private fun mixString(a: String, b: String): String? {
            val max = max(a.length, b.length)
            val mixed = StringBuilder()
            for (i in 0 until max) {
                if (i <= a.length - 1) mixed.append(a, i, i + 1)
                if (i <= b.length - 1) mixed.append(b, i, i + 1)
            }
            return mixed.toString()
        }

        private fun processImages(av1: BufferedImage, av2: BufferedImage): ByteArrayOutputStream {
            try {
                val rng = (0..100).random()
                val template = ImageIO.read(this::class.java.getResource("/boobLove.png"))
                val bg = BufferedImage(template.width, template.height, template.type)
                val image = bg.createGraphics().apply {
                    color = Color(51, 232, 211)
                    font = Font("Whitney", Font.BOLD, 36)
                }

                image.drawImage(av1, 0, 0, 160, 160, null)
                image.drawImage(av2, 320, 0, 160, 160, null)
                image.drawImage(template, 0, 0, null)

                when (rng) {
                    100 -> image.drawString(String.format("%s", rng), 207, 157)
                    in 0..9 -> image.drawString(String.format("%s", rng), 250, 157)
                    else -> image.drawString(String.format("%s", rng), 222, 157)
                }

                image.dispose()

                val stream = ByteArrayOutputStream()
                ImageIO.setUseCache(false)
                ImageIO.write(bg, "png", stream)
                return stream
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
