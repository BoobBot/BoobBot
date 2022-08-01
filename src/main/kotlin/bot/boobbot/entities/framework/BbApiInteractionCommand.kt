package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers
import java.awt.Color
import java.time.Instant

open class BbApiInteractionCommand(private val category: String, private val title: String) : AsyncCommand {
    private val headers = Headers.headersOf("Key", BoobBot.config.BB_API_KEY)

    override suspend fun executeAsync(ctx: Context) {
        val target = ctx.mentions.firstOrNull()
            ?: return ctx.send {
                setColor(Color.red)
                setDescription(Formats.error("you didn't mention a @user, dumbass.\n"))
            }

        if (target.idLong == BoobBot.selfId) {
            return ctx.send("Don't you fucking touch me whore, I will end you.")
        }

        if (target.isBot) {
            return ctx.send("Don't you fucking touch the bots, I will end you.")
        }

        if (target.idLong == ctx.author.idLong) {
            return ctx.send("aww how sad you wanna fuck with yourself, well fucking don't, go find a friend whore.")
        }

        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return ctx.send(Formats.error(" oh? something broken af"))

        ctx.send {
            setTitle(title.format(ctx.author.name, target.name))
            setColor(Colors.getEffectiveColor(ctx.message))
            setImage(res.getString("url"))
            setTimestamp(Instant.now())
        }
    }
}
