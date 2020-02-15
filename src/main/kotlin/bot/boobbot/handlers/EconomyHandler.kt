package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.misc.json
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import okhttp3.Headers
import java.awt.Color
import java.awt.Font
import java.io.ByteArrayOutputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

class EconomyHandler : EventListener {
    private val headers = Headers.of("Key", BoobBot.config.bbApiKey)

    /**
     * @returns the URL to the image.
     */
    fun fetchNsfwImage(category: String): CompletableFuture<String> {
        return BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers)
            .submit()
            .thenApply { it.json() ?: throw IllegalStateException("Response is not JSON") }
            .thenApply { it.getString("url") }
    }

    override fun onEvent(event: GenericEvent) {
        if (event is GuildMessageReceivedEvent) {
            onGuildMessageReceivedEvent(event)
        }
    }

    private fun onGuildMessageReceivedEvent(event: GuildMessageReceivedEvent) {
        if (!event.channel.isNSFW) {
            return
        }

        val number = (0..10000).random()

        //if (number % 59 == 0) {
        if (event.message.contentRaw == "->dodrop") {
            doDrop(event)
        }

        if (event.message.contentRaw == ">grab") {
            event.channel.sendMessage(event.author.asMention + " grab em by the pussy").queue()
        }
    }

    /** DROPS **/
    private fun generateDrop(key: String): CompletableFuture<ByteArrayOutputStream> {
        return fetchNsfwImage("ass")
            .thenCompose { BoobBot.requestUtil.get(it).submit() }
            .thenApply { it.body()?.byteStream() ?: throw IllegalStateException("ResponseBody is null!") }
            .thenApply { ImageIO.read(it) }
            .thenApply {
                val fontSize = it.width * 0.1 // 10% of width

                val graphics = it.createGraphics().apply {
                    font = Font("Whitney", Font.BOLD, fontSize.toInt())
                }

                val metrics = graphics.fontMetrics
                val bounds = metrics.getStringBounds(key, graphics)
                val backgroundColor = Color(0, 0, 0)
                val fontColor = Color(255, 255, 255)

                graphics.color = backgroundColor
                graphics.fillRect(0, 0, bounds.width.toInt() + 20, bounds.height.toInt() + 5)

                graphics.color = fontColor
                graphics.drawString(key, 10, metrics.ascent)

                graphics.dispose()
                it
            }
            .thenApply {
                val stream = ByteArrayOutputStream()
                ImageIO.setUseCache(false)
                ImageIO.write(it, "png", stream)

                stream
            }
    }

    private fun doDrop(event: GuildMessageReceivedEvent) {
        val dropKey = (0..3).map { CHARS.random() }.joinToString("")

        generateDrop(dropKey)
            .thenCompose {
                event.channel.sendMessage("100 <:Tiddies:636967567591997481> have appeared! Use `->grab` to claim them!")
                    .addFile(it.toByteArray(), "drop.png")
                    .submit()
            }
            .thenAccept prompt@ {
                println("here")
                BoobBot.waiter.waitForMessage(
                    { m -> m.channel.idLong == it.channel.idLong && m.contentRaw == "->grab $dropKey" },
                    GRAB_TIMEOUT
                ).thenAccept waiter@ { grabber ->
                    it.delete().queue()
                    if (grabber == null) {
                        return@waiter
                    }

                    // add tiddies
                    grabber.channel.sendMessage("${grabber.author.asMention} claimed 100 <:Tiddies:636967567591997481>").queue()
                }
            }
    }

    companion object {
        private val CHARS = listOf(
            *('a'.toInt()..'z'.toInt()).map { it.toChar() }.toTypedArray(), // a-z
            *('A'.toInt()..'Z'.toInt()).map { it.toChar() }.toTypedArray(), // A-Z
            *(0..9).map { it.toString() }.toTypedArray() // 0-9
        )

        private val GRAB_TIMEOUT = TimeUnit.SECONDS.toMillis(30)
    }
}
