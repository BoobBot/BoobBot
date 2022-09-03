package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Formats.getRemainingCoolDown
import bot.boobbot.utils.Utils
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.time.Instant
import java.time.temporal.ChronoUnit

@CommandProperties(aliases = ["daily", "pd"], description = "Basic daily income.", category = Category.ECONOMY)
class Payday : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {
        val user = BoobBot.database.getUser(event.user.id)

        if (user.lastDaily != null) {
            val t = user.lastDaily!!.plus(6, ChronoUnit.HOURS)
            val now = Instant.now()
            val x = t.toEpochMilli() - now.toEpochMilli()
            if (!t.isBefore((now))) {
                return event.reply("You have to work for it, whore.\nFuck off and try again in ${getRemainingCoolDown(x)}").queue()
            }
        }

        user.lastDaily = Instant.now()
        var rng = (0..50).random()
        val msg = StringBuilder("You got $$rng")
        if (Utils.checkSlashDonor(event)) {
            msg.append(" and an extra $$rng for being a <:p_:475801484282429450>")
            rng += rng
        }
        msg.append("\nTake it and fuck off.")
        user.balance += rng
        user.save()
        event.reply(msg.toString()).queue()
    }

//    fun getRemaining(x: Long): String {
//        val y = 60 * 60 * 1000
//        val h = x / y
//        val m = (x - (h * y)) / (y / 60)
//        val s = (x - (h * y) - (m * (y / 60))) / 1000
//        return String.format("%02d hours, %02d minutes and %02d seconds", h, m, s)
//    }

}
