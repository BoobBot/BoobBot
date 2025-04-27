package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.Choice
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.interactions.commands.OptionType


@CommandProperties(description = "Flip a coin.", aliases = ["flip"], category = Category.ECONOMY, groupByCategory = true)
@Option(name = "side", description = "Heads or tails.", choices = [Choice("Heads", "heads"), Choice("Tails", "tails")])
@Option(name = "bet", description = "Bet amount, 1-500.", type = OptionType.INTEGER)
class Coin : Command {

    override fun execute(ctx: Context) {
        val side =
            ctx.options.getByNameOrNext("side", Resolver.STRING)?.lowercase()?.takeIf { it == "heads" || it == "tails" }
                ?: return ctx.reply(Formats.error("Specify `heads` or `tails`, whore."))

        val amount = ctx.options.getByNameOrNext("bet", Resolver.INTEGER)?.takeIf { it in 1..500 }
            ?: return ctx.reply(Formats.error("Hey whore, Only bets of 1 - 500 are allowed"))

        val u = BoobBot.database.getUser(ctx.user.idLong)

        if (amount > u.balance) {
            return ctx.reply(Formats.error("Hey Whore, You don't have enough money to do this lul, you balance is $${u.balance}"))
        }

        val coinTails = Pair("Tails", "<:tails:681651438664810502>")
        val coinHeads = Pair("Heads", "<:heads:681651442171510812>")

        val rng = (0..9).random()
        val res = if (rng > 4) coinHeads else coinTails

        val msg = if (side == res.first.lowercase()) {
            u.balance += amount
            " You Won $$amount"
        } else {
            u.balance -= amount
            " You Lost $$amount"
        }

        u.save()
        ctx.message { content(Formats.info("`${res.first}`" + msg)) }
        ctx.channel.sendMessage(res.second).queue()
    }

}
