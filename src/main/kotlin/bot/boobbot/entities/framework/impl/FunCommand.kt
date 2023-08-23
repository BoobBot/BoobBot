package bot.boobbot.entities.framework.impl

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils.getRandomFunString
import java.awt.Color
import java.text.MessageFormat

abstract class FunCommand(private val category: String) : AsyncCommand {
    override suspend fun executeAsync(ctx: Context) {
        val target = ctx.options.getByNameOrNext("user", Resolver.CONTEXT_AWARE_USER(ctx))
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("you didn't mention a @user, dumbass.\n"))
            }

        if (target.idLong == BoobBot.selfId) {
            return ctx.reply("Don't you fucking touch me whore, i will end you.")
        }

        if (target.idLong == ctx.user.idLong) {
            return ctx.reply("aww how sad you wanna play with yourself, well fucking don't go find a friend whore.")
        }

        val funString = MessageFormat.format(getRandomFunString(category), ctx.user.asMention, target.asMention)
        ctx.reply(funString)
    }
}
