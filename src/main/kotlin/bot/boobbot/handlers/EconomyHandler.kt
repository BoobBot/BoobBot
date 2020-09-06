package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.Guild
import bot.boobbot.entities.db.User
import bot.boobbot.entities.internals.BoundedThreadPool
import bot.boobbot.utils.json
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.ErrorResponse
import okhttp3.Headers
import java.awt.Color
import java.awt.Font
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO


class EconomyHandler : EventListener {
    private val headers = Headers.of("Key", BoobBot.config.BB_API_KEY)
    private val random = Random()
    private val activeDrops = hashSetOf<Long>()

    private val dropThreads = BoundedThreadPool(
        "DropGen",
        20,
        TimeUnit.MILLISECONDS.toMillis(1),
        500
    )

    private fun random(lower: Int, upper: Int): Int {
        return random.nextInt(upper - lower) + lower
    }


    /**
     * @returns the URL to the image.
     */
    private fun fetchNsfwImage(category: String): CompletableFuture<String> {
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
        val g: Guild by lazy { BoobBot.database.getGuild(event.guild.id) }

        if (!g.dropEnabled || !event.channel.isNSFW) {
            return
        }

        val number = random(0, 10000)

        if (!activeDrops.contains(event.guild.idLong) && number % 59 == 0) {
            doDrop(event)
        }

        if (activeDrops.contains(event.guild.idLong)) {
            if (event.message.contentRaw.startsWith(">grab")) {
                event.message.delete().queue(null, DEFAULT_IGNORE)
            }
        }

        if (event.message.contentRaw == ">coin" && event.message.author.idLong == 248294452307689473L) {
            event.message.delete().reason("User initiated drop").queue(null, DEFAULT_IGNORE)
            doDrop(event)
        }
    }

    /** DROPS **/
    private fun doDrop(event: GuildMessageReceivedEvent) {
        dropThreads.execute { spawnDrop(event) }
    }

    private fun spawnDrop(event: GuildMessageReceivedEvent) {
        val dropKey = (0..3).map { CHARS.random() }.joinToString("")

        generateDrop(dropKey)
            .thenCompose {
                event.channel.sendMessage("Look an ass, grab it! Use `>grab <key>` to grab it!")
                    .addFile(it, "drop.png")
                    .submit()
            }
            .thenCombine(await(event.channel.idLong, dropKey)) { prompt, grabber ->
                activeDrops.remove(event.guild.idLong)
                prompt.delete().queue()

                if (grabber == null) {
                    return@thenCombine
                }

                val user: User by lazy { BoobBot.database.getUser(event.author.id) }
                var found = random(1, 4)
                user.balance += found
                user.save()
                grabber.channel.sendMessage("${grabber.author.asMention} grabbed it and found $$found!")
                    .delay(10, TimeUnit.SECONDS)
                    .flatMap { it.delete() }
                    .queue()

                event.channel.history.retrievePast(100).queue { ms ->
                    val spam = ms.filter { isSpam(it) }
                    if (spam.isEmpty()) {
                        return@queue
                    }
                    if (spam.size < 2) {
                        spam[0].delete().queue()
                        return@queue
                    }
                    event.channel.purgeMessages(*spam.toTypedArray())
                }
            }
    }

    private fun generateDrop(key: String): CompletableFuture<ByteArray> {
        return fetchNsfwImage("ass")
            .thenCompose { BoobBot.requestUtil.get(it).submit() }
            .thenApply { it.body()?.byteStream() ?: throw IllegalStateException("ResponseBody is null!") }
            .thenApply { it.use(ImageIO::read) }
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
                ByteArrayOutputStream().use { s ->
                    ImageIO.setUseCache(false)
                    ImageIO.write(it, "png", s)

                    s.toByteArray()
                }
            }
    }

    private fun await(channelId: Long, dropKey: String) = BoobBot.waiter.waitForMessage(
        { m -> m.channel.idLong == channelId && m.contentRaw == ">grab $dropKey" },
        GRAB_TIMEOUT
    )

    private fun isSpam(message: Message): Boolean {
        return message.contentRaw.toLowerCase().startsWith(">g")
    }

    companion object {
        private val CHARS = listOf(
            *('a'..'z').toList().toTypedArray(),
            *('A'..'Z').toList().toTypedArray(),
            *('0'..'9').toList().toTypedArray()
        )

        private val GRAB_TIMEOUT = TimeUnit.SECONDS.toMillis(30)
        private val DEFAULT_IGNORE = ErrorResponseException.ignore(
            ErrorResponse.MISSING_ACCESS,
            ErrorResponse.MISSING_PERMISSIONS,
            ErrorResponse.UNKNOWN_MESSAGE
        )
    }
}
