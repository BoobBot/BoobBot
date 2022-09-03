package bot.boobbot.slashcommands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.commands.`fun`.Ship.Companion.downloadAvatar
import bot.boobbot.entities.framework.*
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
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
    override fun execute(event: SlashCommandInteractionEvent) {
        if (event.isFromGuild && !event.guild!!.selfMember.hasPermission(
                event.guildChannel,
                Permission.MESSAGE_ATTACH_FILES
            )
        ) {
            return event.reply(Formats.error("I can't send images here, fix it whore.")).queue()
        }

        val user1 = event.getOption("member")!!.asUser
        val user2 = event.getOption("member2")?.asUser ?: event.user

        if (user1.idLong == event.user.idLong && user2.idLong == event.user.idLong) {
            return event.reply("not even you should wanna be shipped with yourself").queue()
        }

        if (user1.idLong == user2.idLong) {
            return event.reply("how the fuck are you going to ship the same person whore").queue()
        }

        if (user1.idLong == event.jda.selfUser.idLong || user2.idLong == event.jda.selfUser.idLong) {
            return event.reply("Don't you fucking touch me whore, I will end you.").queue()
        }

        event.deferReply().queue()

        val av1Fut = downloadAvatar(user1.effectiveAvatarUrl)
        val av2Fut = downloadAvatar(user2.effectiveAvatarUrl)

        av1Fut.thenCombine(av2Fut) { av1, av2 ->
            val result = processImages(av1, av2)
            val content = MessageCreateBuilder()
                .setContent(newMixString(user1.name, user2.name))
                .addContent(" <:icon:676613489548197915>")
                .build()

            event.hook.sendMessage(content)
                .addFiles(FileUpload.fromData(result.toByteArray(), "shipped.png"))
                .submit()
                .whenComplete { _, _ -> result.close() }
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
