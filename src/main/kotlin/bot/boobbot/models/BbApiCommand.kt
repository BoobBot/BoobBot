package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.json
import okhttp3.Headers
import java.time.Instant

abstract class BbApiCommand(private val category: String) : AsyncCommand {

    private val headers = Headers.of("Key", BoobBot.config.bbApiKey)

    override suspend fun executeAsync(ctx: Context) {
        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

        val link = res.getString("url")
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.embed {
            setTitle("${Formats.LEWD_EMOTE} No image? Click me!", link)
            setColor(Colors.getEffectiveColor(ctx.message))
            setImage(link)
            setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }
}
