package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.misc.Database
import bot.boobbot.misc.Formats
import bot.boobbot.misc.json
import net.dv8tion.jda.api.entities.Message
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
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

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
        val user = BoobBot.database.getUser(event.author.id)
        user.messagesSent = user.messagesSent + 1
        if (!user.blacklisted) {
            if (user.inJail) {
                user.jailRemaining = min(user.jailRemaining - 1, 0)
                if (user.jailRemaining == 0) {
                    user.inJail = false
                }
                BoobBot.database.setUser(user)
            }
            if (event.message.textChannel.isNSFW) {
                val tags = Formats.tag
                val tagSize = tags.filter { event.message.contentDisplay.contains(it.toString()) }.size
                user.lewdPoints = user.lewdPoints + min(tagSize, 5) * (user.balance / 100) * .01.toInt()
                user.nsfwMessagesSent = user.nsfwMessagesSent + 1
            }
            if (user.coolDownCount >= (0..10).random()) {
                user.coolDownCount = (0..10).random()
                user.experience =
                    if (user.bonusXp != null && user.bonusXp!! > 0) user.experience + 2 else user.experience + 1
                if (user.bonusXp != null && user.bonusXp!! > 0) {
                    user.bonusXp = user.bonusXp!! - 1
                }
            }

            user.level = floor(0.1 * sqrt(user.experience.toDouble())).toInt()
            user.lewdLevel = calculateLewdLevel(user)
            BoobBot.database.setUser(user)
            // }
        }

        val g = BoobBot.database.getGuild(event.guild.id)!!
        //if (!g.dropEnabled) {
          //  return
       // }
        if (!event.channel.isNSFW) {
            return
        }

        val number = (0..10000).random()

        if (number % 59 == 0) {
            doDrop(event)
        }

        if (event.message.contentRaw == ">grab") {
            event.channel.sendMessage(event.author.asMention + " grab em by the pussy").queue()

        }
        if (event.message.contentRaw == ">coin" && event.message.author.idLong == 248294452307689473L) {
            event.message.delete().reason("yes").queue()
            doDrop(event)


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
                event.channel.sendMessage("Look an Ass, Grab it! Use `>grab <key>` to Grab it!")
                    .addFile(it.toByteArray(), "drop.png")
                    .submit()
            }
            .thenAccept prompt@{
                println("here")
                BoobBot.waiter.waitForMessage(
                    { m -> m.channel.idLong == it.channel.idLong && m.contentRaw == ">grab $dropKey" },
                    GRAB_TIMEOUT
                ).thenAccept waiter@{ grabber ->
                    it.delete().queue()
                    event.channel.history.retrievePast(100).queue { ms ->
                        val spam = ms.filter { isSpam(it) }
                        if (spam.isEmpty()) {
                            return@queue
                        }
                        if (spam.size <= 2) {
                            spam[0].delete().queue()
                            return@queue
                        }
                        event.channel.purgeMessages(*spam.toTypedArray())
                    }
                    if (grabber == null) {
                        return@waiter
                    }

                    // add tiddies
                    val user = BoobBot.database.getUser(event.author.id)
                    user.balance+=(0..5).random()
                    BoobBot.database.setUser(user)
                    grabber.channel.sendMessage("${grabber.author.asMention} Grabbed it!")
                        .delay(10, TimeUnit.SECONDS)
                        .flatMap { it.delete() }
                        .queue()
                }
            }


    }


    private fun calculateLewdLevel(user: Database.User): Int {
        val calculateLewdPoints =
            (user.experience / 100) * .1 +
                    (user.nsfwCommandsUsed / 100) * .3 -
                    (user.commandsUsed / 100) * .3 +
                    (user.lewdPoints / 100) * 20
        // lewd level up
        return floor(0.1 * sqrt(calculateLewdPoints)).toInt()
    }


    private fun isSpam(message: Message): Boolean {
        return message.contentRaw.toLowerCase().startsWith(">g")
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
