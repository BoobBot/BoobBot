package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.User
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping

@CommandProperties(aliases = [], description = "banking operations \uD83C\uDFE6", guildOnly = true, category = Category.ECONOMY)
class Bank : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {
        return when (event.subcommandName) {
            "deposit" -> deposit(event)
            "withdraw" -> withdraw(event)
            "balance" -> balance(event)
            "transfer" -> transfer(event)
            else -> {}
        }
    }

    fun deposit(event: SlashCommandInteractionEvent) {
        val user = BoobBot.database.getUser(event.user.id)

        if (event.getOption("amount")!!.asInt > user.balance) {
            return event.reply(Formats.error("wtf whore, you only have ${user.balance}")).queue()
        }

        user.balance -= event.getOption("amount")!!.asInt
        user.bankBalance += event.getOption("amount")!!.asInt
        user.save()
        event.reply("Deposited  $${event.getOption("amount")!!.asInt}, You now have $${user.bankBalance} in the bank.").queue()
    }


    fun withdraw(event: SlashCommandInteractionEvent) {
        val user = BoobBot.database.getUser(event.user.id)

        if (event.getOption("amount")!!.asInt > user.balance) {
            return event.reply(Formats.error("wtf whore, you only have ${user.balance}")).queue()
        }

        user.balance -= event.getOption("amount")!!.asInt
        user.bankBalance += event.getOption("amount")!!.asInt
        user.save()
        event.reply("Withdrew $${event.getOption("amount")!!.asInt}, You now have $${user.bankBalance} in the bank.").queue()
    }

    fun balance(event: SlashCommandInteractionEvent) {
        val user: User by lazy { BoobBot.database.getUser(event.user.id) }
        event.reply("You are carrying $${user.balance} and have $${user.bankBalance} in the bank.").queue()
    }

    fun transfer(event: SlashCommandInteractionEvent) {
        val user = BoobBot.database.getUser(event.user.id)
        val recipient = event.getOption("member", OptionMapping::getAsMember)!!
        val amount = event.getOption("amount", OptionMapping::getAsInt)!!

        if (recipient.idLong == event.user.idLong) {
            user.bankBalance -= 10
            user.save()
            return event.reply("Don't be a whore, you cant transfer to yourself. I took $10 from you for trying.").queue()
        }

        if (amount > user.bankBalance) {
            return event.reply("wtf whore, you only have $${user.bankBalance} in your bank account").queue()
        }

        val user2 = BoobBot.database.getUser(recipient.id)

        user.bankBalance -= amount
        user2.bankBalance += amount
        user.save()
        user2.save()

        event.reply("Transferred $$amount to ${recipient.user.asTag}, You now have $${user.bankBalance} in the bank.").queue()
    }

}
