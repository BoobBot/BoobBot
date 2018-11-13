package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.*
import java.time.Instant

abstract class BbApiCommand(private val category: String) : AsyncCommand {
    override suspend fun executeAsync(ctx: Context) {
        val headers = createHeaders(
            Pair("Key", Constants.BB_API_KEY)
        )

        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

        ctx.embed {
            setDescription(Formats.LEWD_EMOTE)
            setColor(Colors.getEffectiveColor(ctx.message))
            setImage(res.getString("url"))
            setFooter("Requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }
}
