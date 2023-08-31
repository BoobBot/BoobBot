package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.Options
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(
    description = "Play the slot machine.",
    aliases = ["slots"],
    category = Category.ECONOMY,
    groupByCategory = true
)
@Options(
    [
        Option(name = "bet", description = "Bet amount, 1-500.", type = OptionType.INTEGER)
    ]
)
class Slot : Command {

    private val slotEmotes = listOf("🍒", "🍋", "🍊", "🍇", "🔔", "💰")

    override fun execute(ctx: Context) {
        val amount = ctx.options.getByNameOrNext("bet", Resolver.INTEGER)?.takeIf { it in 1..500 }
            ?: return ctx.reply(Formats.error("Hey whore, Only bets of 1 - 500 are allowed"))

        val u = BoobBot.database.getUser(ctx.user.id)

        if (amount > u.balance) {
            return ctx.reply(Formats.error("Hey Whore, You don't have enough money to do this, your balance is $${u.balance}"))
        }

        val results = (0..2).map { slotEmotes.random() }

        val hasWin = results.toSet().size == 1

        val msg = if (hasWin) {
            u.balance += amount * 5
            " You won $$amount! 🎉"
        } else {
            u.balance -= amount
            " You lost $$amount. 💸"
        }

        u.save()
        val formattedResults = results.joinToString(" ")
        ctx.message { content(Formats.info("Slot Results: $formattedResults$msg")) }
    }
}