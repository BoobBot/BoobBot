package bot.boobbot.commands.interactions

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import net.dv8tion.jda.api.interactions.commands.OptionType
import okhttp3.Headers.Companion.headersOf
import java.awt.Color
import java.time.Instant

@CommandProperties(description = "Make someone your sub.", category = Category.INTERACTIONS, nsfw = true)
@Option(name = "user", description = "The user to spank.", type = OptionType.USER)
class Sub : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        val target = ctx.options.getByNameOrNext("user", Resolver.CONTEXT_AWARE_USER(ctx))
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


        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/sub", headersOf("Key", BoobBot.config.BB_API_KEY))
            .await()
            ?.json()
            ?: return ctx.reply(Formats.error(" oh? something broken af"))

        ctx.reply {
            setTitle("<:sub:866437395318833193> ${ctx.user.name} Makes ${target.name} their sub.")
            setColor(Colors.getEffectiveColor(ctx.member))
            setImage(res.getString("url"))
            setTimestamp(Instant.now())
        }

    }
}
