package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils.getRandomFunString
import java.awt.Color
import java.text.MessageFormat

abstract class FunCommand(private val category: String) : AsyncCommand {
    override suspend fun executeAsync(ctx: Context) {
        val target = ctx.mentions.firstOrNull()
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
