package bot.boobbot.slashcommands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO


@CommandProperties(description = "Shipped", category = Category.FUN)
class Ship : SlashCommand {
    override fun execute(ctx: SlashContext) {
        if (ctx.isFromGuild && !ctx.guild!!.selfMember.hasPermission(ctx.guildChannel!!, Permission.MESSAGE_ATTACH_FILES)) {
            return ctx.reply(Formats.error("I can't send images here, fix it whore."))
        }

        val user1 = ctx.getOption("member")!!.asUser
        val user2 = ctx.getOption("member2")?.asUser ?: ctx.user

        if (user1.idLong == ctx.user.idLong && user2.idLong == ctx.user.idLong) {
            return ctx.reply("not even you should wanna be shipped with yourself")
        }

        if (user1.idLong == user2.idLong) {
            return ctx.reply("how the fuck are you going to ship the same person whore")
        }

        if (user1.idLong == ctx.jda.selfUser.idLong || user2.idLong == ctx.jda.selfUser.idLong) {
            return ctx.reply("Don't you fucking touch me whore, I will end you.")
        }

        ctx.defer()

        val av1Fut = downloadAvatar(user1.effectiveAvatarUrl)
        val av2Fut = downloadAvatar(user2.effectiveAvatarUrl)

        av1Fut.thenCombine(av2Fut) { av1, av2 ->
            val result = processImages(av1, av2)
            val content = MessageCreateBuilder()
                .setContent(newMixString(user1.name, user2.name))
                .addContent(" <:icon:676613489548197915>")
                .addFiles(FileUpload.fromData(result.toByteArray(), "shipped.png"))

            ctx.reply(content, success = { result.close() }, failure = { result.close() })
        }
    }

    companion object {
        fun downloadAvatar(url: String): CompletableFuture<BufferedImage> {
            return BoobBot.requestUtil.get(url).submit()
                .thenApply { it.body?.byteStream() ?: throw IllegalStateException("ResponseBody is null!") }
                .thenApply { ImageIO.read(it) }
        }

        private fun newMixString(a: String, b: String): String? {
            val mixed = StringBuilder()
            var i = 0
            while (i < a.length || i < b.length) {
                if (i < a.length) mixed.append(a[i])
                if (i < b.length) mixed.append(b[i])
                i++
            }
            return mixed.toString()
        }

        private fun processImages(av1: BufferedImage, av2: BufferedImage): ByteArrayOutputStream {
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
        }
    }
}
