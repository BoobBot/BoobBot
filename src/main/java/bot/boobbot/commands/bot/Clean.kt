package bot.boobbot.commands.bot

import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.utils.MiscUtil
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors


@CommandProperties(
        description = "Cleans up all the bot and trigger messages",
        aliases = ["clean", "cleanup", "purge", "del"],
        guildOnly = true,
        category = CommandProperties.category.MISC
)
class Clean : Command {

    private fun twoWeeks(message: Message): Boolean {
        val twoWeeksAgo = System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000 - MiscUtil.DISCORD_EPOCH shl MiscUtil.TIMESTAMP_OFFSET.toInt()
        return MiscUtil.parseSnowflake(message.id) < twoWeeksAgo
    }

    private fun isSpam(message: Message): Boolean {
        return message.jda.selfUser === message.author || message.contentDisplay.startsWith("!bb")
    }

    override fun execute(ctx: Context) {
        if (!ctx.botCan(Permission.MESSAGE_MANAGE)) {
            return ctx.send("\uD83D\uDEAB Hey whore, I lack the `MANAGE_MESSAGES` permission needed to do this")
        }

        if (!ctx.userCan(Permission.MESSAGE_MANAGE)) {
            return ctx.send("\uD83D\uDEAB Hey whore, you lack the `MANAGE_MESSAGES` permission needed to do this")
        }

        ctx.message.delete().queue()
        ctx.message.channel.history.retrievePast(100).queue({ ms ->
            val spam = ms.stream().filter { m -> !twoWeeks(m) && isSpam(m) }.collect(Collectors.toList())
            if (spam.isEmpty()) {
                return@queue
            }
            if (spam.size <= 2) {
                spam[0].delete().queue()
                return@queue
            }
            ctx.message.textChannel.deleteMessages(spam).queue(null, null)
            ctx.message.channel.sendMessage(Formats.info("I deleted ${spam.size} messages")).queue({ m -> m.delete().queueAfter(5, TimeUnit.SECONDS) }, null)


        }, null)
    }

}
