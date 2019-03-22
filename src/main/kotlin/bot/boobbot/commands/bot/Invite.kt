package bot.boobbot.commands.bot

import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import java.time.Instant

@CommandProperties(description = "Bot and support guild links", aliases = ["join", "oauth", "link", "links", "support"])
class Invite : Command {

    override fun execute(ctx: Context) {
        ctx.embed {
            color(Colors.rndColor)
            setAuthor(ctx.selfUser.name, ctx.selfUser.effectiveAvatarUrl, ctx.selfUser.effectiveAvatarUrl)
            description(Formats.LING_MSG)
            setFooter("Requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }

}