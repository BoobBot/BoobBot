package bot.boobbot.entities.framework.impl

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers
import java.awt.Color
import java.time.Instant

abstract class BbApiInteractionCommand(private val category: String, private val title: String) : AsyncCommand {
    private val headers = Headers.headersOf("Key", BoobBot.config.BB_API_KEY)

    override suspend fun executeAsync(ctx: Context) {
        val target = ctx.options.getByNameOrNext("with", Resolver.USER)
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("you didn't mention a @user, dumbass.\n"))
            }

        if (target.idLong == BoobBot.selfId) {
            return ctx.reply("Don't you fucking touch me whore, I will end you.")
        }

        if (target.isBot) {
            return ctx.reply("Don't you fucking touch the bots, I will end you.")
        }

        if (target.idLong == ctx.user.idLong) {
            return ctx.reply("aww how sad you wanna fuck with yourself, well fucking don't, go find a friend whore.")
        }

        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return ctx.reply(Formats.error(" oh? something broken af"))

        ctx.reply(title.format(ctx.user.asMention, target.asMention)+"\n"+res.getString("url"))
        //ctx.reply {
       //     setTitle(title.format(ctx.user.asMention, target.asMention))
         //   setColor(Colors.getEffectiveColor(ctx.member))
         //   setImage(res.getString("url"))
       //    setTimestamp(Instant.now())
      //  }


    }
}
