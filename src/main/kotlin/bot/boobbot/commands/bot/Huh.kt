package bot.boobbot.commands.bot

import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.utils.Colors
import java.time.Instant

@CommandProperties(description = "Seems you don't discord.", aliases = ["howto"], groupByCategory = true)
class Huh : Command {

    override fun execute(ctx: Context) {
        ctx.reply {
            setColor(Colors.getEffectiveColor(ctx.member))
            setTitle("It seems you don't discord, So here is a gif")
            setAuthor(ctx.selfUser.name, ctx.selfUser.effectiveAvatarUrl, ctx.selfUser.effectiveAvatarUrl)
            setImage("https://b1nzy-banned.me/g/V6Aeh.gif")
            setFooter(
                "LOL if that cant help ${ctx.user.name}, Maybe this can \uD83D\uDD2B",
                ctx.user.effectiveAvatarUrl
            )
            setTimestamp(Instant.now())
        }
    }

}