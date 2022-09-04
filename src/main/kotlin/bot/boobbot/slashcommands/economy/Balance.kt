package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats

@CommandProperties(description = "See your current balance.", aliases = ["bal", "$"], category = Category.ECONOMY)
class Balance : SlashCommand {
    override fun execute(ctx: SlashContext) {
        val user = ctx.getOption("member")?.asUser ?: ctx.user
        val u = BoobBot.database.getUser(user.id)

        ctx.reply {
            setColor(Colors.rndColor)
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
