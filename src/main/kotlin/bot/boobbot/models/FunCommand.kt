package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils.Companion.getRandomFunString
import java.awt.Color
import java.text.MessageFormat

abstract class FunCommand(private val category: String) : AsyncCommand {
    override suspend fun executeAsync(ctx: Context) {
        val target = ctx.message.mentionedUsers.firstOrNull()
            ?: return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("you didn't mention a @user, dumbass.\n"))
            }

        if (target.idLong == BoobBot.selfId) {
            return ctx.send("Don't you fucking touch me whore, i will end you.")
        }

        if (target.idLong == ctx.author.idLong) {
            return ctx.send("aww how sad you wanna play with yourself, well fucking don't go find a friend whore.")
        }

        val funString = MessageFormat.format(getRandomFunString(category), ctx.author.name, target.name)
        ctx.send(funString)
    }
}
