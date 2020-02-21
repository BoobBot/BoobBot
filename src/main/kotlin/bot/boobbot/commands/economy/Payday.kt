package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import java.time.Instant
import java.time.temporal.ChronoUnit


@CommandProperties(aliases = ["daily", "pd"], description = "Basic daily income.", category = Category.ECONOMY)
class Payday : Command {

    override fun execute(ctx: Context) {
        val user = BoobBot.database.getUser(ctx.author.id)

        if (user.lastDaily != null) {
            val t = user.lastDaily!!.plus(1, ChronoUnit.MINUTES)
            val now = Instant.now()
            val x = t.toEpochMilli() - now.toEpochMilli()
            if (!t.isBefore((now))) {
                return ctx.send("You have to work for it, whore.\nFuck off and try again in ${getRemaining(x)}")
            }
        }

        user.lastDaily = Instant.now()
        user.save()

        var rng = (0..50).random()
        val msg = StringBuilder("You got $$rng")

        if (bot.boobbot.misc.Utils.checkDonor(ctx.message)) {
            msg.append(" and an extra $$rng for being a <:p_:475801484282429450>\n")
            rng += rng
        }

        msg.append("Take it and fuck off.")

        user.balance += rng
        user.save()
        ctx.send(msg.toString())
    }

    fun getRemaining(x: Long): String {
        val y = 60 * 60 * 1000
        val h = x / y
        val m = (x - (h * y)) / (y / 60)
        val s = (x - (h * y) - (m * (y / 60))) / 1000

        return String.format("%02d hours, %02d minutes and %02d seconds", h, m, s)
    }

}
