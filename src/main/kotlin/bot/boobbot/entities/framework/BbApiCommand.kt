package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncCommand
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers
import java.time.Instant

abstract class BbApiCommand(private val category: String) : AsyncCommand {

    private val headers = Headers.of("Key", BoobBot.config.BB_API_KEY)

    override suspend fun executeAsync(ctx: Context) {
        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

        val link = res.getString("url")
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.embed {
            setTitle("${Formats.LEWD_EMOTE} Click me!", "https://discord.boob.bot")
            setColor(Colors.getEffectiveColor(ctx.message))
            setImage(link)
            setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }
}
