package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(description = "Flip a coin.", aliases = ["flip"], category = Category.ECONOMY)
class Coin : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {
        val u = BoobBot.database.getUser(event.user.id)
        val bet = event.getOption("bet", OptionMapping::getAsInt)!!

        if (bet > u.balance) {
            return event.reply(Formats.error("Hey Whore, You don't have enough money to do this lul, you balance is $${u.balance}")).queue()
        }

        val coinTails = Pair("Tails", "<:tails:681651438664810502>")
        val coinHeads = Pair("Heads", "<:heads:681651442171510812>")
        val rng = (0..9).random()
        val res = if (rng > 4) coinHeads else coinTails
        val msg: String
        if (event.getOptionsByType(OptionType.STRING).first().asString == res.first.lowercase()) {
            u.balance += bet
            msg = " You Won $$bet"
        } else {
            u.balance -= bet
            msg = " You Lost $$bet"
        }
        u.save()
        event.channel.sendMessage(res.second).queue()
        event.deferReply().queue()
        event.hook.sendMessage(Formats.info(res.first + msg)).queue()
    }

}
