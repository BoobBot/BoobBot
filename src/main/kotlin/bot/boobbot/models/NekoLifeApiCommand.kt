package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.json
import java.time.Instant

abstract class NekoLifeApiCommand(private val category: String) : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {

        val res = BoobBot.requestUtil.get("https://nekos.life/api/v2/img/$category").await()?.json()
                ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

        ctx.embed {
            setTitle("Nya~", "https://nekos.life")
            setColor(Colors.getEffectiveColor(ctx.message))
            setImage(res.getString("url"))
            setFooter("Powered by nekos.life", "https://nekos.life/static/icons/favicon-194x194.png")
            setTimestamp(Instant.now())
        }

    }
}
