package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import java.awt.Color
import java.time.Instant
import java.time.temporal.ChronoUnit


@CommandProperties(aliases = ["+"],description = "Give rep.", category = Category.ECONOMY)
class Rep : Command {

    override fun execute(ctx: Context) {
        val target = ctx.mentions.firstOrNull()
            ?: return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("you didn't mention a @user, dumbass.\n"))
            }

        if (target.idLong == BoobBot.selfId) {
            return ctx.send("Don't you fucking touch me whore, I will end you.")
        }

        if (target.idLong == ctx.author.idLong) {
            return ctx.send("aww how sad you wanna rep yourself, well fucking don't. Go find a friend whore.")
        }
        val author = BoobBot.database.getUser(ctx.author.id)
        val now = Instant.now()
        if (author.lastRep != null) {
            val t = author.lastRep!!.plus(12, ChronoUnit.HOURS )
            val x = t.toEpochMilli() - now.toEpochMilli()
            if (!t.isBefore((now))) {
                return ctx.send("You already gave rep today whore.\nFuck off and try again in ${getRemaining(x)}")
            }
        }
        author.lastRep = now
        author.save()
        val user = BoobBot.database.getUser(target.id)
        user.rep += 1
        user.save()
        val msg = "You gave ${target.asTag} a rep point, Good job! Seems you're not completely useless after all."
        ctx.send(msg)
    }

    fun getRemaining(x: Long) :String{
        val y = 60 * 60 * 1000
        val h = x / y
        val m = (x - (h * y)) / (y / 60)
        val s = (x - (h * y) - (m * (y / 60))) / 1000
        var r = ""
        if (h >0 ) r +="$h Hours "
        if (m > 0) r +="$m Minutes "
        if (s > 0) r += "$s Seconds "
        return r

    }

}
