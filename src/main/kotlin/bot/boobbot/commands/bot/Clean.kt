package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.Resolver
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.concurrent.TimeUnit


@CommandProperties(
    description = "Cleans up all the bot and trigger messages",
    aliases = ["clean", "cleanup", "purge", "del"],
    guildOnly = true,
    category = Category.MISC
)
@Option("amount", description = "The maximum number of messages to remove. Limit of 500.", type = OptionType.INTEGER)
class Clean : Command {
    private fun filterDeletable(ctx: Context, messages: List<Message>): List<Message> {
        val selfId = ctx.selfUser.idLong
        val prefixes = mutableListOf(
            BoobBot.defaultPrefix,
            "<@$selfId>",
            "<@!$selfId>"
        )

        return messages.filter {
            it.author.idLong == it.jda.selfUser.idLong || prefixes.any { p -> it.contentRaw.lowercase().startsWith(p) } || it.referencedMessage?.author?.idLong == selfId
        }
    }

    override fun execute(ctx: Context) {
        if (!ctx.botCan(Permission.MESSAGE_MANAGE)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, I lack the `MANAGE_MESSAGES` permission needed to do this")
        }

        if (!ctx.botCan(Permission.MESSAGE_HISTORY)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, I lack the `MESSAGE_HISTORY` permission needed to do this")
        }

        if (!ctx.userCan(Permission.MESSAGE_MANAGE)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, you lack the `MANAGE_MESSAGES` permission needed to do this")
        }

        val amount = ctx.options.getByNameOrNext("amount", Resolver.INTEGER) ?: 100

        ctx.channel.iterableHistory
            .takeAsync(amount)
            .thenApply { filterDeletable(ctx, it) }
            .thenApply { ctx.channel.purgeMessages(it) }
            .thenAccept { ctx.reply("Channel cleaned, whore. Want me to cook you dinner too?", true) }
    }
}
