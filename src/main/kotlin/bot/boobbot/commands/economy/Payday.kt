package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import java.time.Instant
import java.time.temporal.ChronoUnit


@CommandProperties(aliases = ["daily", "pd"],description = "Basic income", category = Category.ECONOMY)
class Payday : Command {

    override fun execute(ctx: Context) {
        val user = BoobBot.database.getUser(ctx.author.id)
        if (user.lastDaily != null) {
            val t = user.lastDaily!!.plus(1, ChronoUnit.MINUTES )
            val now = Instant.now()
            val x = t.toEpochMilli() - now.toEpochMilli()
            if (!t.isBefore((now))) {
                return ctx.send("You already did it whore\nFuck off and try again in ${getRemaining(x)}")
            }
        }
        user.lastDaily = Instant.now()
        user.save()
        var rng = (0..50).random()
        var msg = "You got $rng$"
        if (bot.boobbot.misc.Utils.checkDonor(ctx.message)) {
            msg += ", Plus $rng$ for being a <:p_:475801484282429450>"
            rng += rng
        }

        user.balance += rng
        user.save()
        msg += "\nTake it and fuck off."
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
