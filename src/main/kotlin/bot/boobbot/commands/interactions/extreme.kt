package bot.boobbot.commands.interactions

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf
import java.awt.Color
import java.time.Instant

@CommandProperties(description = "Are you extreme?", category = Category.INTERACTIONS, nsfw = true)
class extreme : AsyncCommand {


    override suspend fun executeAsync(ctx: Context) {

        val target = ctx.mentions.firstOrNull()
            ?: return ctx.embed {
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


        val res =
            BoobBot.requestUtil.get("https://boob.bot/api/v2/img/extreme", headersOf("Key", BoobBot.config.BB_API_KEY))
                .await()?.json()
                ?: return ctx.send(
                    Formats.error(" oh? something broken af")
                )

        ctx.embed {
            setTitle("<:extreme:866451714471231510> ${ctx.author.name} likes it extreme. Are you in  ${target.name}?")
            setColor(Colors.getEffectiveColor(ctx.message))
            setImage(res.getString("url"))
            setTimestamp(Instant.now())
        }

    }
}