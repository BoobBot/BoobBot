package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.misc.BoundedThreadPool
import bot.boobbot.misc.Formats
import bot.boobbot.misc.json
import bot.boobbot.models.economy.User
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt


class EconomyHandler : EventListener {
    private val headers = Headers.of("Key", BoobBot.config.bbApiKey)
    private val random = Random()
    private val activeDrops = hashSetOf<Long>()

    private val dropThreads = BoundedThreadPool("DropGen", 20, TimeUnit.MILLISECONDS.toMillis(1), 500)

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
        val user = BoobBot.database.getUser(event.author.id)
        user.messagesSent++
        if (!user.blacklisted) {
            if (user.inJail) {
                user.jailRemaining = min(user.jailRemaining - 1, 0)
                user.inJail = user.jailRemaining > 0
            }

            if (event.message.textChannel.isNSFW) {
                val tagSize = Formats.tag.count { event.message.contentDisplay.contains(it) }
                user.lewdPoints += min(tagSize, 5) * (user.balance / 100) * .01.toInt()
                user.nsfwMessagesSent++
            }

            if (user.coolDownCount >= random(0, 10)) {
                user.coolDownCount = random(0, 10)
                user.experience++

                if (user.bonusXp != null && user.bonusXp!! > 0) {
                    user.experience++ // extra XP
                    user.bonusXp = user.bonusXp!! - 1
                }
            }

            user.level = floor(0.1 * sqrt(user.experience.toDouble())).toInt()
            user.lewdLevel = calculateLewdLevel(user)
            user.save()
        }

        val g = BoobBot.database.getGuild(event.guild.id)

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

                val user = BoobBot.database.getUser(event.author.id)
                user.balance += random(1, 4)
                user.save()
                grabber.channel.sendMessage("${grabber.author.asMention} grabbed it!")
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

    private fun calculateLewdLevel(user: User): Int {
        val calculateLewdPoints =
            (user.experience / 100) * .1 +
                    (user.nsfwCommandsUsed / 100) * .3 -
                    (user.commandsUsed / 100) * .3 +
                    (user.lewdPoints / 100) * 20
        // lewd level up
        return floor(0.1 * sqrt(calculateLewdPoints)).toInt()
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
