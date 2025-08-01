package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats.getRemainingCoolDown
import bot.boobbot.utils.Utils
import java.time.Instant
import java.time.temporal.ChronoUnit


@CommandProperties(aliases = ["daily", "pd"], description = "Basic daily income.", category = Category.ECONOMY, groupByCategory = true)
class Payday : Command {
    override fun execute(ctx: Context) {
        val user = BoobBot.database.getUser(ctx.user.idLong, partial = true)
        val lastDaily = user.lastDaily

        val nextClaim = lastDaily.plus(6, ChronoUnit.HOURS)
        val now = Instant.now()
        val cooldown = nextClaim.toEpochMilli() - now.toEpochMilli()
        if (!nextClaim.isBefore(now)) {
            return ctx.reply("You have to work for it, whore.\nFuck off and try again in ${getRemainingCoolDown(cooldown)}")
        }

        user.lastDaily = Instant.now()
        var rng = (10..500).random().toLong()
        val msg = StringBuilder("You got $$rng")

        if (Utils.checkDonor(ctx)) {
            val prng = (1..100).random()
            msg.append(" and an extra $$prng for being a <:p_:475801484282429450>")
            rng += prng
        }

        msg.append("\nTake it and fuck off.")
        user.balance += rng
        ctx.reply(msg.toString())
    }

//    fun getRemaining(x: Long): String {
//        val y = 60 * 60 * 1000
//        val h = x / y
//        val m = (x - (h * y)) / (y / 60)
//        val s = (x - (h * y) - (m * (y / 60))) / 1000
//        return String.format("%02d hours, %02d minutes and %02d seconds", h, m, s)
//    }

}
