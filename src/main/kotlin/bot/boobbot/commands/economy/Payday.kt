package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import java.time.Instant
import java.time.temporal.ChronoUnit


@CommandProperties(aliases = ["daily"],description = "Basic income", category = Category.ECONOMY)
class Payday : Command {

    override fun execute(ctx: Context) {
        val user = BoobBot.database.getUser(ctx.author.id)
        if (user.lastdaily != null) {
            val t = user.lastdaily!!.plus(6, ChronoUnit.HOURS)
            val now = Instant.now()
            val x = t.toEpochMilli() - now.toEpochMilli()
            val y = 60 * 60 * 1000
            val h = x / y
            val m = (x - (h * y)) / (y / 60)
            val s = (x - (h * y) - (m * (y / 60))) / 1000
            if (!t.isBefore((now))) {
                return ctx.send("Try again in $h:$m:$s")
            }
        }
        user.lastdaily = Instant.now()
        user.save()
        var rng = (0..50).random()
        if (bot.boobbot.misc.Utils.checkDonor(ctx.message)) rng += rng
        user.balance += rng
        user.save()
        ctx.send("you got $rng$")
    }


}
