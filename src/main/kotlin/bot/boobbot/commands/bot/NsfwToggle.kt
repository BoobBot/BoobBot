package bot.boobbot.commands.bot

import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.thenException
import com.mewna.catnip.entity.util.Permission

@CommandProperties(
    description = "Toggles the current channels nsfw setting",
    aliases = ["nsfw", "toggle"],
    guildOnly = true,
    category = Category.MISC
)
class NsfwToggle : Command {

    override fun execute(ctx: Context) {
        if (!ctx.botCan(Permission.MANAGE_CHANNELS)) {
            return ctx.send("\uD83D\uDEAB Hey whore, I lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        if (!ctx.userCan(Permission.MANAGE_CHANNELS)) {
            return ctx.send("\uD83D\uDEAB Hey whore, you lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        val nsfwStatus = !ctx.textChannel!!.nsfw()

        ctx.textChannel.edit().nsfw(nsfwStatus).submit()
            .thenAccept {
                val changed = if (nsfwStatus) "allowed" else "disallowed"
                ctx.send("NSFW on this channel is now $changed")
            }
            .thenException {
                ctx.send("shit something broke\n\n$it")
            }
    }

}