package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Command
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats


@CommandProperties(description = "See your current balance.", aliases = ["bal", "$"], category = Category.ECONOMY)
class Balance : Command {

    override fun execute(ctx: Context) {
        val user = ctx.mentions.firstOrNull() ?: ctx.author
        val u = BoobBot.database.getUser(user.id)

        ctx.send {
            setColor(Colors.getEffectiveColor(ctx.message))
            addField(
                Formats.info("**Balance Information**"),
                "**Current Balance**: $${u.balance}\n" +
                        "**Bank Balance**: $${u.bankBalance}\n" +
                        "**Total Assets**: $${(u.balance + u.bankBalance)}",
                false
            )
        }
    }

}
