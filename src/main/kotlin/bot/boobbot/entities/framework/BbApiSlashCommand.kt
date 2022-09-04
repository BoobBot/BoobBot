package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers.Companion.headersOf
import java.time.Instant

abstract class BbApiSlashCommand(private val category: String) : AsyncSlashCommand {

    private val headers = headersOf("Key", BoobBot.config.BB_API_KEY)

    override suspend fun executeAsync(ctx: SlashContext) {
        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return ctx.reply("\uD83D\uDEAB oh? something broken af")

        val link = res.getString("url")
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.reply {
            setTitle("${Formats.LEWD_EMOTE} Click me!", "https://discord.boob.bot")
            setColor(Colors.rndColor)
            setImage(link)
            setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }
}
