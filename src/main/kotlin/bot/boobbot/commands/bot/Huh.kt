package bot.boobbot.commands.bot

import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import java.time.Instant

@CommandProperties(description = "Seems you don't discord.", aliases = ["howto"])
class Huh : Command {

    override fun execute(ctx: Context) {
        ctx.embed {
            color(Colors.getEffectiveColor(ctx.message))
            title("It seems you don't discord, So here is a gif")
            author(ctx.selfUser?.username(), ctx.selfUser?.effectiveAvatarUrl(), ctx.selfUser?.effectiveAvatarUrl())
            image("https://b1nzy-banned.me/g/V6Aeh.gif")
            footer(
                "LOL if that cant help ${ctx.author.username()}, Maybe this can \uD83D\uDD2B",
                ctx.author.effectiveAvatarUrl()
            )
            timestamp(Instant.now())
        }
    }

}