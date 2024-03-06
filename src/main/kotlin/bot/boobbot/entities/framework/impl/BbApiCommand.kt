package bot.boobbot.entities.framework.impl

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers.Companion.headersOf
import java.time.Instant

abstract class BbApiCommand(private val category: String) : AsyncCommand {
    protected val headers = headersOf("Key", BoobBot.config.BB_API_KEY)

    override suspend fun executeAsync(ctx: Context) {
        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return ctx.reply("\uD83D\uDEAB oh? something broken af")

        val link = res.getString("url")
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.reply {
            setTitle("${Formats.LEWD_EMOTE} Click me!", "https://discord.boob.bot")
            setColor(Colors.getEffectiveColor(ctx.member))
            setImage(link)
            setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }
}
