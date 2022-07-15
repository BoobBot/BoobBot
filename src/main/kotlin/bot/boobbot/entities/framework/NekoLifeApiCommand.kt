package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncCommand
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import java.time.Instant

abstract class NekoLifeApiCommand(private val category: String) : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {

        val res = BoobBot.requestUtil.get("https://nekos.life/api/v2/img/$category").await()?.json()
            ?: return ctx.send(
                Formats.error(" oh? something broken af")
            )

        ctx.send {
            setTitle("Nya~", "https://nekos.life")
            setColor(Colors.getEffectiveColor(ctx.message))
            setImage(res.getString("url"))
            setFooter("Powered by https://nekos.life", "https://nekos.life/static/icons/favicon-194x194.png")
            setTimestamp(Instant.now())
        }

    }
}
