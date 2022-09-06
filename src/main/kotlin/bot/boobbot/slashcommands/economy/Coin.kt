package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(description = "Flip a coin.", aliases = ["flip"], category = Category.ECONOMY)
class Coin : SlashCommand {
    override fun execute(ctx: SlashContext) {
        val u = BoobBot.database.getUser(ctx.user.id)
        val bet = ctx.getOption("bet", OptionMapping::getAsInt)!!

        if (bet > u.balance) {
            return ctx.reply(Formats.error("Hey Whore, You don't have enough money to do this lul, you balance is $${u.balance}"))
        }

        val coinTails = Pair("Tails", "<:tails:681651438664810502>")
        val coinHeads = Pair("Heads", "<:heads:681651442171510812>")
        val rng = (0..9).random()
        val res = if (rng > 4) coinHeads else coinTails
        val msg: String
        if (ctx.getOptionsByType(OptionType.STRING).first().asString == res.first.lowercase()) {
            u.balance += bet
            msg = " You Won $$bet"
        } else {
            u.balance -= bet
            msg = " You Lost $$bet"
        }
        u.save()
        ctx.reply(res.second)
        ctx.event.hook.sendMessage(Formats.info(res.first + msg)).queue()
    }
}
