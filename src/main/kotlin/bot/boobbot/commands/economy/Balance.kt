package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.interactions.commands.OptionType


@CommandProperties(description = "See your current balance.", aliases = ["bal", "$"], category = Category.ECONOMY, groupByCategory = true)
@Option(name = "user", description = "The user to check the balance of. Defaults to you.", type = OptionType.USER, required = false)
class Balance : Command {

    override fun execute(ctx: Context) {
        val user = ctx.options.getByNameOrNext("user", Resolver.USER) ?: ctx.user
        val u = BoobBot.database.getUser(user.id)

        ctx.reply {
            setColor(Colors.getEffectiveColor(ctx.member))
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
