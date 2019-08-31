package bot.boobbot.commands.bot

import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import net.dv8tion.jda.api.Permission

@CommandProperties(
    description = "Toggles the current channels nsfw setting",
    aliases = ["nsfw", "toggle"],
    guildOnly = true,
    category = Category.MISC
)
class NsfwToggle : Command {

    override fun execute(ctx: Context) {
        if (!ctx.botCan(Permission.MANAGE_CHANNEL)) {
            return ctx.send("\uD83D\uDEAB Hey whore, I lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        if (!ctx.userCan(Permission.MANAGE_CHANNEL)) {
            return ctx.send("\uD83D\uDEAB Hey whore, you lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        val nsfwStatus = !ctx.textChannel!!.isNSFW

        ctx.textChannel.manager.setNSFW(nsfwStatus).queue({
            val changed = if (nsfwStatus) "allowed" else "disallowed"
            ctx.send("NSFW on this channel is now $changed")
        }, {
            ctx.send("shit something broke\n\n$it")
        })
    }

}