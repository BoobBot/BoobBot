package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers
import java.awt.Color
import java.time.Instant

abstract class BbApiInteractionSlashCommand(private val category: String, private val title: String) : AsyncSlashCommand {
    private val headers = Headers.headersOf("Key", BoobBot.config.BB_API_KEY)

    override suspend fun executeAsync(ctx: SlashContext) {
        val target = ctx.options.firstOrNull()?.asMember
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("you didn't mention a @user, dumbass.\n"))
            }

        if (target.idLong == BoobBot.selfId) {
            return ctx.reply("Don't you fucking touch me whore, i will end you.")
        }

        if (target.idLong == ctx.member!!.idLong) {
            return ctx.reply("aww how sad you wanna play with yourself, well fucking don't go find a friend whore.")
        }

        if (target.user.isBot) {
            return ctx.reply("Don't you fucking touch the bots, I will end you.")
        }

        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return ctx.reply(Formats.error(" oh? something broken af"))

        ctx.reply {
            setTitle(title.format(ctx.user.name, target.user.name))
            setColor(Colors.rndColor)
            setImage(res.getString("url"))
            setTimestamp(Instant.now())
        }
    }
}
