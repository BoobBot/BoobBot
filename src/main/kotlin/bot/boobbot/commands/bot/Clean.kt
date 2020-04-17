package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import java.util.concurrent.TimeUnit


@CommandProperties(
    description = "Cleans up all the bot and trigger messages",
    aliases = ["clean", "cleanup", "purge", "del"],
    guildOnly = true,
    category = Category.MISC
)
class Clean : Command {

    private val DISCORD_EPOCH = 1420070400000L
    private val TIMESTAMP_OFFSET = 22

    private fun parseSnowflake(input: String): Long {
        try {
            return if (!input.startsWith("-"))
                java.lang.Long.parseUnsignedLong(input)
            else
                java.lang.Long.parseLong(input)
        } catch (ex: NumberFormatException) {
            throw NumberFormatException(
                String.format("The specified ID is not a valid snowflake (%s). Expecting a valid long value!", input)
            )
        }
    }

    private fun twoWeeks(message: Message): Boolean {
        val twoWeeksAgo = System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000 - DISCORD_EPOCH shl TIMESTAMP_OFFSET
        return parseSnowflake(message.id) < twoWeeksAgo
    }

    private fun isSpam(message: Message, guildId: String): Boolean {
        val prefixes = mutableListOf(
            BoobBot.defaultPrefix,
            "<@${message.jda.selfUser.id}> ",
            "<@!${message.jda.selfUser.id}> "
        )

        val custom = BoobBot.database.getPrefix(guildId)

        if (custom != null) {
            prefixes.add(custom)
        }

        return message.author.idLong == message.jda.selfUser.idLong ||
                prefixes.any { message.contentRaw.toLowerCase().startsWith(it) }
    }

    override fun execute(ctx: Context) {
        if (!ctx.botCan(Permission.MESSAGE_MANAGE)) {
            return ctx.send("\uD83D\uDEAB Hey whore, I lack the `MANAGE_MESSAGES` permission needed to do this")
        }

        if (!ctx.botCan(Permission.MESSAGE_HISTORY)) {
            return ctx.send("\uD83D\uDEAB Hey whore, I lack the `MESSAGE_HISTORY` permission needed to do this")
        }

        if (!ctx.userCan(Permission.MESSAGE_MANAGE)) {
            return ctx.send("\uD83D\uDEAB Hey whore, you lack the `MANAGE_MESSAGES` permission needed to do this")
        }

        ctx.channel.history.retrievePast(100).queue { ms ->
            val spam = ms.filter { !twoWeeks(it) && isSpam(it, ctx.guild!!.id) }

            if (spam.isNotEmpty() && spam.size <= 2) {
                spam.forEach { msg -> msg.delete().queue() }
            } else if (spam.isNotEmpty()) {
                ctx.channel.purgeMessages(*spam.toTypedArray())
            }

            ctx.channel.sendMessage(Formats.info("I deleted ${spam.size} messages"))
                .queue { m -> m.delete().queueAfter(5, TimeUnit.SECONDS) }
        }
    }

}
