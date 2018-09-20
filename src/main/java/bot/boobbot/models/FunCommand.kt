package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.*
import bot.boobbot.misc.Utils.Companion.getRandomFunString
import java.awt.Color

abstract class FunCommand(private val category: String) : AsyncCommand {
    override suspend fun executeAsync(ctx: Context) {
        if (ctx.message.mentionedUsers.isEmpty()) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("you didn't mention a @user, dumbass.\n"))
            }
        }
        val user = ctx.message.mentionedUsers.firstOrNull()
        if (user?.idLong == ctx.selfUser.idLong){return ctx.send("Don't you fucking touch me whore, i will end you.")}
        if(user?.idLong == ctx.author.idLong){return ctx.send("aww how sad you wanna play with yourself, well fucking don't go find a friend whore.")}
        ctx.send(getRandomFunString(category).replace("{0}",ctx.author.name, true).replace("{1}", user?.name.toString(), true))
    }
}