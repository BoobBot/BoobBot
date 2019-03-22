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
            author(ctx.selfUser?.username(), ctx.selfUser?.effectiveAvatarUrl(), ctx.selfUser?.effectiveAvatarUrl())
            description(Formats.LING_MSG)
            footer("Requested by ${ctx.author.username()}", ctx.author.effectiveAvatarUrl())
            timestamp(Instant.now())
        }
    }

}