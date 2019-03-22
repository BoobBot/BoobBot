package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import com.mewna.catnip.entity.message.Message
import com.mewna.catnip.entity.util.Permission
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors


@CommandProperties(
    description = "Cleans up all the bot and trigger messages",
    aliases = ["clean", "cleanup", "purge", "del"],
    guildOnly = true,
    category = Category.MISC
)
class Clean : Command {

    private val DISCORD_EPOCH = 1420070400000L
    private val TIMESTAMP_OFFSET = 22

    fun parseSnowflake(input: String): Long {
        try {
            return if (!input.startsWith("-"))
            // if not negative -> parse unsigned
                java.lang.Long.parseUnsignedLong(input)
            else
            // if negative -> parse normal
                java.lang.Long.parseLong(input)
        } catch (ex: NumberFormatException) {
            throw NumberFormatException(
                String.format("The specified ID is not a valid snowflake (%s). Expecting a valid long value!", input)
            )
        }
    }

    private fun twoWeeks(message: Message): Boolean {
        val twoWeeksAgo = System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000 - DISCORD_EPOCH shl TIMESTAMP_OFFSET
        return parseSnowflake(message.id()) < twoWeeksAgo
    }

    private fun isSpam(message: Message): Boolean {
        return BoobBot.selfId == message.author().idAsLong() ||
                message.content().toLowerCase().startsWith(if (BoobBot.isDebug) "!bb" else "bb")
    }

    override fun execute(ctx: Context) {
        if (!ctx.botCan(Permission.MANAGE_MESSAGES)) {
            return ctx.send("\uD83D\uDEAB Hey whore, I lack the `MANAGE_MESSAGES` permission needed to do this")
        }

        if (!ctx.botCan(Permission.READ_MESSAGE_HISTORY)) {
            return ctx.send("\uD83D\uDEAB Hey whore, I lack the `MESSAGE_HISTORY` permission needed to do this")
        }

        if (!ctx.userCan(Permission.MANAGE_MESSAGES)) {
            return ctx.send("\uD83D\uDEAB Hey whore, you lack the `MANAGE_MESSAGES` permission needed to do this")
        }

        ctx.message.delete()
        ctx.channel.fetchMessages().limit(100).fetch().thenAccept { ms ->
            val spam = ms.filter { !twoWeeks(it) && isSpam(it) }

            if (spam.isEmpty()) {
                return@thenAccept
            }

            if (spam.size <= 2) {
                spam[0].delete()
                return@thenAccept
            }

            ctx.catnip.rest().channel().deleteMessages(ctx.channel.id(), spam.map { it.id() })

            ctx.send(Formats.info("I deleted ${spam.size} messages"))
                //.queue({ m -> m.delete().queueAfter(5, TimeUnit.SECONDS) }, null)


        }
    }

}
