package bot.boobbot.commands.bot

import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import java.time.Instant

@CommandProperties(description = "Seems you don't discord.", aliases = ["howto"])
class huh : Command {

    override fun execute(ctx: Context) {
        ctx.embed {
            setColor(Colors.getEffectiveColor(ctx.message))
            setTitle("It seems you don\'t discord, So here is a gif")
            setAuthor(ctx.selfUser.name, ctx.selfUser.effectiveAvatarUrl, ctx.selfUser.effectiveAvatarUrl)
            setImage("https://b1nzy-banned.me/g/V6Aeh.gif")
            setFooter("LOL if that cant help ${ctx.author.name}, Maybe this can \uD83D\uDD2B", ctx.author.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }

}