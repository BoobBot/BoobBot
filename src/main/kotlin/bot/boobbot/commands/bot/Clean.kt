package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
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
    private fun filterDeletable(ctx: MessageContext, messages: List<Message>): List<Message> {
        val selfId = ctx.jda.selfUser.idLong
        val prefixes = mutableListOf(
            BoobBot.defaultPrefix,
            "<@$selfId>",
            "<@!$selfId>"
        )

        return messages.filter {
            it.author.idLong == it.jda.selfUser.idLong || prefixes.any { p -> it.contentRaw.lowercase().startsWith(p) } || it.referencedMessage?.author?.idLong == selfId
        }
    }

    override fun execute(ctx: MessageContext) {
        if (!ctx.botCan(Permission.MESSAGE_MANAGE)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, I lack the `MANAGE_MESSAGES` permission needed to do this")
        }

        if (!ctx.botCan(Permission.MESSAGE_HISTORY)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, I lack the `MESSAGE_HISTORY` permission needed to do this")
        }

        if (!ctx.userCan(Permission.MESSAGE_MANAGE)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, you lack the `MANAGE_MESSAGES` permission needed to do this")
        }

        ctx.channel.iterableHistory
            .takeAsync(100)
            .thenApply { filterDeletable(ctx, it) }
            .thenApply { ctx.channel.purgeMessages(it) }
            .thenAccept { ctx.reply("Channel cleaned, whore. Want me to cook you dinner too?", true) }
    }
}
