package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.entities.internals.BoundedThreadPool
import bot.boobbot.utils.json
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.ErrorResponse
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import okhttp3.Headers.Companion.headersOf
import java.awt.Color
import java.awt.Font
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.imageio.ImageIO

class BootyDropper : EventListener {
    private val headers = headersOf("Key", BoobBot.config.BB_API_KEY)

    private val random = Random()
    private val activeDrops = ConcurrentHashMap<Long, ActiveDrop>()

    private val dropThreads = BoundedThreadPool(
        "DropGen",
        20,
        TimeUnit.MILLISECONDS.toMillis(1),
        500
    )

    private fun random(lower: Int, upper: Int): Int {
        return random.nextInt(upper - lower) + lower
    }

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is MessageReceivedEvent -> processMessage(event)
            is ButtonInteractionEvent -> processDropInteraction(event)
        }
    }

    private fun hasActiveDrop(guildId: Long): Boolean {
        val active = activeDrops[guildId]
            ?: return false

        if (active.expired) {
            active.expire()
            return false
        }

        return true
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


    private fun processMessage(event: MessageReceivedEvent) {
        if (event.author.isBot || !event.isFromGuild || event.channelType != ChannelType.TEXT || !event.channel.asTextChannel().isNSFW) {
            return
        }

        val number = random(0, 10000)

        val isManualDrop = event.message.contentRaw.startsWith(">coin") && event.message.author.idLong in BoobBot.owners
        val shouldDrop = isManualDrop || (!hasActiveDrop(event.guild.idLong) && BoobBot.database.getGuild(event.guild.idLong).dropEnabled && number % 59 == 0)

        if (shouldDrop) {
            dropThreads.execute { spawnDrop(event) }

            if (isManualDrop) {
                event.message.delete().reason("User initiated drop").queue(null, DEFAULT_IGNORE)
            }
        }
    }

    private fun processDropInteraction(event: ButtonInteractionEvent) {
        if (!event.isFromGuild || !event.componentId.startsWith("drop")) {
            return
        }

        if (!hasActiveDrop(event.guild!!.idLong)) {
            return event.reply("There is no drop to claim, whore.").setEphemeral(true).queue()
        }

        val drop = activeDrops[event.guild!!.idLong]
            ?: return event.reply("There is no drop to claim, whore.").setEphemeral(true).queue()

        drop.claim(event.user)
    }

    private fun spawnDrop(event: MessageReceivedEvent) {
        BoobBot.log.info("Dropped on ${event.guild.name}/${event.guild.id}")
        val dropKey = generateDropKey()

        generateDrop(dropKey)
            .thenCompose {
                event.channel.sendMessage("Look an ass, grab it! Click the button with the matching code!")
                    .addFiles(FileUpload.fromData(it, "drop.png"))
                    .addComponents(generateButtons(dropKey))
                    .submit()
            }
            .thenAccept {
                activeDrops[event.guild.idLong] = ActiveDrop(dropKey, it)
            }
    }

    private fun generateDrop(key: String): CompletableFuture<ByteArray> {
        return fetchNsfwImage("ass")
            .thenCompose { BoobBot.requestUtil.get(it).submit() }
            .thenApply { it.body.byteStream() }
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

    private fun generateButtons(dropKey: String, count: Int = 10): List<ActionRow> {
        val buttons = arrayOfNulls<Button>(count)
        val targetIndex = random.nextInt(count)

        for (i in 0 until count) {
            val label = dropKey.takeIf { i == targetIndex }
                ?: generateDropKey(not = dropKey)

            buttons[i] = Button.primary("drop:$i", label)
        }

        // splits our buttons into multiple rows, each holding the maximum number of components
        // as defined by their type (i.e. rows of 5 buttons)
        return ActionRow.partitionOf(*buttons)
    }

    /**
     * Generates a drop key, with an optional parameter to prevent generating keys the same as
     * a legitimate drop key.
     */
    private fun generateDropKey(not: String? = null): String {
        var generated: String

        do {
            generated = (0..3).map { CHARS.random() }.joinToString("")
        } while (generated == not)

        return generated
    }

    inner class ActiveDrop(val dropKey: String, private val dropMessage: Message) {
        // initialize drop expiry [GRAB_TIMEOUT] seconds from now.
        private val expiresAt = System.nanoTime() + TimeUnit.SECONDS.toNanos(GRAB_TIMEOUT_SECONDS)

        val expired: Boolean
            get() = System.nanoTime() >= expiresAt

        private val claimable = AtomicBoolean(true)

        init {
            executor.schedule(::expire, GRAB_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        }

        fun expire() {
            activeDrops.remove(dropMessage.guildIdLong)

            if (!claimable.compareAndSet(true, false)) {
                // something else beat us to this, so we return to avoid overwriting
                // any current state. (thread safe)
                return
            }

            val message = MessageEditBuilder().setContent("No one claimed this drop!")
                .setReplace(true)
                .build()

            dropMessage.editMessage(message)
                .delay(5, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue(null, DEFAULT_IGNORE)
        }

        fun claim(user: User) {
            activeDrops.remove(dropMessage.guildIdLong)

            if (!claimable.compareAndSet(true, false)) {
                // something else beat us to this, so we return to avoid overwriting
                // any current state. (thread safe)
                return
            }

            val userData = BoobBot.database.getUser(user.idLong)
            val found = random(1, 4)
            userData.balance += found
            userData.save()

            dropMessage.delete().queue(null, DEFAULT_IGNORE)

            dropMessage.channel.sendMessage("${user.asMention} grabbed it and found $$found!")
                .delay(10, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue(null, DEFAULT_IGNORE)
        }
    }

    companion object {
        private val CHARS = listOf(
            *('a'..'z').toList().toTypedArray(),
            *('A'..'Z').toList().toTypedArray(),
            *('0'..'9').toList().toTypedArray()
        )

        private const val GRAB_TIMEOUT_SECONDS = 30L
        private val DEFAULT_IGNORE = ErrorResponseException.ignore(
            ErrorResponse.MISSING_ACCESS,
            ErrorResponse.MISSING_PERMISSIONS,
            ErrorResponse.UNKNOWN_MESSAGE
        )

        private val executor = Executors.newSingleThreadScheduledExecutor()
    }
}
