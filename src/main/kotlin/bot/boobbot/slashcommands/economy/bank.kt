package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.User
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.security.Permission


@CommandProperties(aliases = [], description = "banking operations \uD83C\uDFE6", guildOnly = true, category = Category.ECONOMY)
class bank : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {
        if(event.subcommandName.toString() == "deposit"){
            return deposit(event)
        }
        if(event.subcommandName.toString() == "withdraw"){
            return withdraw(event)
        }
        if(event.subcommandName.toString() == "balance"){
            return balance(event)
        }
        if(event.subcommandName.toString() == "transfer"){
            return transfer(event)
        }

    }

    fun deposit(event: SlashCommandInteractionEvent) {
        val user: User by lazy { BoobBot.database.getUser(event.user.id) }
        if (event.getOption("amount")!!.asInt > user.balance) {
            return event.reply(Formats.error("wtf whore, you only have ${user.balance}")).queue()
        }
        user.balance -= event.getOption("amount")!!.asInt
        user.bankBalance += event.getOption("amount")!!.asInt
        user.save()
        event.reply("Deposited  $${event.getOption("amount")!!.asInt}, You now have $${user.bankBalance} in the bank.").queue()
    }


    fun withdraw(event: SlashCommandInteractionEvent) {
        val user: User by lazy { BoobBot.database.getUser(event.user.id) }
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

        val user: User by lazy { BoobBot.database.getUser(event.user.id) }

        if (event.getOption("member")!!.asUser.id == event.user.id) {
            user.bankBalance -= 10
            user.save()
            return event.reply("Don't be a whore, you cant transfer to yourself. I took $10 from you for trying.").queue()
        }

        if (event.getOption("amount")!!.asInt > user.bankBalance) {
            return event.reply("wtf whore, you only have $${user.bankBalance} in your bank account").queue()
        }

        val user2: User by lazy { BoobBot.database.getUser(event.getOption("member")!!.asUser.id) }

        user.bankBalance -= event.getOption("amount")!!.asInt
        user2.bankBalance += event.getOption("amount")!!.asInt
        user.save()
        user2.save()

        event.reply("Transferred $${event.getOption("amount")!!.asInt} to ${event.getOption("member")!!.asUser.asTag}, You now have $${user.bankBalance} in the bank.").queue()
    }


}


