package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Formats.getRemainingCoolDown
import java.awt.Color
import java.time.Instant
import java.time.temporal.ChronoUnit

@CommandProperties(aliases = ["+"], description = "Give rep.", category = Category.ECONOMY)
class Rep : SlashCommand {

    override fun execute(ctx: SlashContext) {
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

        val author = BoobBot.database.getUser(ctx.user.id)
        val now = Instant.now()

        if (author.lastRep != null) {
            val t = author.lastRep!!.plus(12, ChronoUnit.HOURS)
            val x = t.toEpochMilli() - now.toEpochMilli()
            if (!t.isBefore(now)) {
                return ctx.reply("You already gave rep today whore.\nFuck off and try again in ${getRemainingCoolDown(x)}")
            }
        }

        author.lastRep = now
        author.save()

        BoobBot.database.getUser(target.id)
            .apply { rep += 1 }
            .save()

        ctx.reply("You gave ${target.asMention} a rep point, Good job! Seems you're not completely useless after all.")
    }

}
