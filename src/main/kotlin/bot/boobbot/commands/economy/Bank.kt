package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.Options
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(aliases = ["bank"], description = "banking operations \uD83C\uDFE6", guildOnly = true, category = Category.ECONOMY, groupByCategory = true)
class Bank : Command {

    override fun execute(ctx: Context) {
        sendSubcommandHelp(ctx)
    }

    @SubCommand(aliases = ["dep"], description = "deposit funds. \uD83D\uDCB0")
    @Option(name = "amount", description = "The amount to deposit.", type = OptionType.INTEGER)
    fun deposit(ctx: Context) {
        val amount = ctx.options.getByNameOrNext("amount", Resolver.INTEGER)
            ?: return ctx.reply("wtf, i don't mind-read. Specify how much to deposit, whore.")

        if (amount < 0) {
            return ctx.reply("wtf are you doing?")
        }

        val user = BoobBot.database.getUser(ctx.user.id)

        if (amount > user.balance) {
            return ctx.reply("wtf whore, you only have ${user.balance}")
        }

        user.balance -= amount
        user.bankBalance += amount
        user.save()

        ctx.reply("Deposited $$amount, You now have $${user.bankBalance} in the bank.")
    }

    @SubCommand(aliases = ["with"], description = "withdraw funds. \uD83D\uDCB8")
    @Option(name = "amount", description = "The amount to withdraw.", type = OptionType.INTEGER)
    fun withdraw(ctx: Context) {
        val amount = ctx.options.getByNameOrNext("amount", Resolver.INTEGER)
            ?: return ctx.reply("wtf, i don't mind-read. Specify how much to withdraw, whore.")

        if (amount < 0) {
            return ctx.reply("wtf are you doing?")
        }

        val user = BoobBot.database.getUser(ctx.user.id)

        if (amount > user.bankBalance) {
            return ctx.reply("wtf whore, you only have ${user.bankBalance}")
        }

        user.bankBalance -= amount
        user.balance += amount
        user.save()

        ctx.reply("Withdrew $$amount, You now have $${user.bankBalance} in the bank.")
    }

    @SubCommand(aliases = ["bal"], description = "check your funds. \uD83D\uDCB3")
    fun balance(ctx: Context) {
        val user = BoobBot.database.getUser(ctx.user.id)
        ctx.reply("You are carrying $${user.balance} and have $${user.bankBalance} in the bank.")
    }

    @SubCommand(aliases = ["trans"], description = "transfer funds. ⇆")
    @Options([ // TODO: Revisit
        Option(name = "amount", description = "The amount to transfer.", type = OptionType.INTEGER),
        Option(name = "to", description = "The user to transfer to.", type = OptionType.USER)
    ])
    fun transfer(ctx: Context) {
        val amount = ctx.options.getByNameOrNext("amount", Resolver.INTEGER)
            ?: return ctx.reply("wtf, i don't mind read. Specify how much to deposit, whore.")

        val to = ctx.options.getByNameOrNext("to", Resolver.CONTEXT_AWARE_USER(ctx))
            ?: return ctx.reply("How in the fuck would i know who you want to transfer to if you don't give me a valid mention? try `@BoobBot bank transfer 500 @user#0000`")

        if (amount < 0) {
            return ctx.reply("wtf are you doing?")
        }

        val user = BoobBot.database.getUser(ctx.user.id)

        if (to.idLong == ctx.user.idLong) {
            user.bankBalance -= 10
            user.save()
            return ctx.reply("Don't be a whore, you cant transfer to yourself. I took $10 from you for trying.")
        }

        if (amount > user.bankBalance) {
            return ctx.reply("wtf whore, you only have $${user.bankBalance} in your bank account")
        }

        val user2 = BoobBot.database.getUser(to.id)

        user.bankBalance -= amount
        user2.bankBalance += amount
        user.save()
        user2.save()

        ctx.reply("Transferred $$amount to ${to.asTag}, You now have $${user.bankBalance} in the bank.")
    }
}
