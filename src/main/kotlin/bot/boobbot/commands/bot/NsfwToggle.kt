package bot.boobbot.commands.bot

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import net.dv8tion.jda.api.Permission

@CommandProperties(
    description = "Toggles the current channels nsfw setting",
    aliases = ["toggle"],
    guildOnly = true,
    category = Category.MISC
)
class NsfwToggle : Command {

    override fun execute(ctx: Context) {
        if (!ctx.botCan(Permission.MANAGE_CHANNEL)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, I lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        if (!ctx.userCan(Permission.MANAGE_CHANNEL)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, you lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        val nsfwStatus = ctx.textChannel?.isNSFW?.not()
            ?: return ctx.reply("This isn't a text-channel that supports NSFW, whore.")

        ctx.textChannel.manager.setNSFW(nsfwStatus).queue({
            val changed = if (nsfwStatus) "allowed" else "disallowed"
            ctx.reply("NSFW on this channel is now $changed")
        }, {
            ctx.reply("shit something broke\n\n$it")
        })
    }

}