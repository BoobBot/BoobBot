package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.interactions.commands.OptionMapping

@CommandProperties(aliases = [], description = "banking operations \uD83C\uDFE6", guildOnly = true, category = Category.ECONOMY)
class Bank : SlashCommand {

    override fun execute(ctx: SlashContext) {
        return when (ctx.subcommandName) {
            "deposit" -> deposit(ctx)
            "withdraw" -> withdraw(ctx)
            "balance" -> balance(ctx)
            "transfer" -> transfer(ctx)
            else -> {}
        }
    }

    fun deposit(ctx: SlashContext) {
        val user = BoobBot.database.getUser(ctx.user.id)
        val amount = ctx.getOption("amount", OptionMapping::getAsInt)!!

        if (amount > user.balance) {
            return ctx.reply(Formats.error("wtf whore, you only have ${user.balance}"))
        }

        user.balance -= amount
        user.bankBalance += amount
        user.save()
        ctx.reply("Deposited  $$amount, You now have $${user.bankBalance} in the bank.")
    }


    fun withdraw(ctx: SlashContext) {
        val user = BoobBot.database.getUser(ctx.user.id)
        val amount = ctx.getOption("amount", OptionMapping::getAsInt)!!

        if (amount > user.bankBalance) {
            return ctx.reply(Formats.error("wtf whore, you only have ${user.bankBalance}"))
        }

        val newBankBalance = user.bankBalance - amount
        val newBalance = user.balance + amount
        user.bankBalance = newBankBalance
        user.balance = newBalance
        user.save()
        ctx.reply("Withdrew $$amount, You now have $$newBalance on you and $$newBankBalance in the bank.")
    }

    fun balance(ctx: SlashContext) {
        val user = BoobBot.database.getUser(ctx.user.id)
        ctx.reply("You are carrying $${user.balance} and have $${user.bankBalance} in the bank.")
    }

    fun transfer(ctx: SlashContext) {
        val user = BoobBot.database.getUser(ctx.user.id)
        val recipient = ctx.getOption("member", OptionMapping::getAsMember)!!
        val amount = ctx.getOption("amount", OptionMapping::getAsInt)!!

        if (recipient.idLong == ctx.user.idLong) {
            user.bankBalance -= 10
            user.save()
            return ctx.reply("Don't be a whore, you cant transfer to yourself. I took $10 from you for trying.")
        }

        if (amount > user.bankBalance) {
            return ctx.reply("wtf whore, you only have $${user.bankBalance} in your bank account")
        }

        val user2 = BoobBot.database.getUser(recipient.id)

        user.bankBalance -= amount
        user2.bankBalance += amount
        user.save()
        user2.save()

        ctx.reply("Transferred $$amount to ${recipient.user.asTag}, You now have $${user.bankBalance} in the bank.")
    }

}
