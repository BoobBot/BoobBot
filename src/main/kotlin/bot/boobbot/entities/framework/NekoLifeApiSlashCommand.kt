package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import java.time.Instant

abstract class NekoLifeApiSlashCommand(private val category: String) : AsyncSlashCommand {
    override suspend fun executeAsync(ctx: SlashContext) {
        val res = BoobBot.requestUtil.get("https://nekos.life/api/v2/img/$category").await()?.json()
            ?: return ctx.reply(Formats.error(" oh? something broken af"))

        ctx.reply {
            setTitle("Nya~", "https://nekos.life")
            setColor(Colors.rndColor)
            setImage(res.getString("url"))
            setFooter("Powered by https://nekos.life", "https://nekos.life/static/icons/favicon-194x194.png")
            setTimestamp(Instant.now())
        }

    }
}
