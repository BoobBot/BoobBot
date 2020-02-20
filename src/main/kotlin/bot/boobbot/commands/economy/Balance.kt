package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Formats.progressPercentage
import kotlin.math.pow


@CommandProperties(description = "get a balance", category = Category.ECONOMY)
class Balance : Command {

    override fun execute(ctx: Context) {
        val user = ctx.mentions.firstOrNull() ?: ctx.author
        val u = BoobBot.database.getUser(user.id)
        val em = ctx.embed {
            setColor(Colors.getEffectiveColor(ctx.message))
            addField(
                Formats.info("**Balance Information**"), "" +
                        "**Current Balance**: ${u.balance}$\n" +
                        "**Bank Balance**: ${u.bankBalance}$\n" +
                        "**Total Assets**: ${(u.balance + u.bankBalance)}$", false
            )

        }

    }
}
