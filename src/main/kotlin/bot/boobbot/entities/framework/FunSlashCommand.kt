package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils.getRandomFunString
import java.awt.Color
import java.text.MessageFormat

abstract class FunSlashCommand(private val category: String) : AsyncSlashCommand {
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

        val funString = MessageFormat.format(getRandomFunString(category), ctx.member.effectiveName, target.effectiveName)
        ctx.reply(funString)
    }
}
