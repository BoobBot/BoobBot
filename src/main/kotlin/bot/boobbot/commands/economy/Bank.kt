package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.User
import bot.boobbot.entities.framework.*
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.interfaces.Command

@CommandProperties(aliases = ["bank"], description = "banking operations \uD83C\uDFE6", guildOnly = true, category = Category.ECONOMY)
class Bank : Command {

    override fun execute(ctx: MessageContext) {
        sendSubcommandHelp(ctx)
    }

    @SubCommand(aliases = ["dep"], description = "deposit funds. \t\uD83D\uDCB0")
    fun deposit(ctx: MessageContext) {
        if (ctx.args.isEmpty()) {
            return ctx.reply("wtf, i don't mind read. Specify how much to deposit, whore.")
        }

        if (ctx.args[0].toIntOrNull() == null) {
            return ctx.reply("wtf whore, thats not a number")
        }
        val user = BoobBot.database.getUser(ctx.user.id)

        if (ctx.args[0].toInt() < 0){
            return ctx.reply("wtf are you doing?")
        }

        if (ctx.args[0].toInt() > user.balance) {
            return ctx.reply("wtf whore, you only have ${user.balance}")
        }

        user.balance -= ctx.args[0].toInt()
        user.bankBalance += ctx.args[0].toInt()
        user.save()

        ctx.reply("Deposited $${ctx.args[0]}, You now have $${user.bankBalance} in the bank.")
    }

    @SubCommand(aliases = ["with"], description = "withdraw funds. \uD83D\uDCB8")
    fun withdraw(ctx: MessageContext) {
        if (ctx.args.isEmpty()) {
            return ctx.reply("wtf, i don't mind read. Specify how much to withdraw, whore.")
        }

        if (ctx.args[0].toIntOrNull() == null) {
            return ctx.reply("wtf whore, thats not a number")
        }
        val user = BoobBot.database.getUser(ctx.user.id)

        if (ctx.args[0].toInt() < 0){
            return ctx.reply("wtf are you doing?")
        }

        if (ctx.args[0].toInt() > user.bankBalance) {
            return ctx.reply("wtf whore, you only have ${user.bankBalance}")
        }

        user.bankBalance -= ctx.args[0].toInt()
        user.balance += ctx.args[0].toInt()
        user.save()

        ctx.reply("Withdrew $${ctx.args[0]}, You now have $${user.bankBalance} in the bank.")
    }

    @SubCommand(aliases = ["bal"], description = "check your funds. \uD83D\uDCB3")
    fun balance(ctx: MessageContext) {
        val user = BoobBot.database.getUser(ctx.user.id)
        ctx.reply("You are carrying $${user.balance} and have $${user.bankBalance} in the bank.")
    }

    @SubCommand(aliases = ["trans"], description = "transfer funds. ⇆")
    fun transfer(ctx: MessageContext) {
        if (ctx.mentions.isEmpty()) {
            return ctx.reply("How in the fuck would i know who you want to transfer to if you don't give me a valid mention? `try: bbbank transfer 500 @user#0000`")
        }

        if (ctx.args[0].isEmpty()) {
            return ctx.reply("wtf, i don't mind read. Specify how much to deposit, whore.")
        }

        if (ctx.args[0].toInt() < 0){
            return ctx.reply("wtf are you doing?")
        }

        if (ctx.args[0].toIntOrNull() == null) {
            return ctx.reply("wtf whore, that's not a number. `try: bbbank transfer 500 @user#0000`")
        }

        val user: User by lazy { BoobBot.database.getUser(ctx.user.id) }

        if (ctx.mentions[0].id == ctx.user.id) {
            user.bankBalance -= 10
            user.save()
            return ctx.reply("Don't be a whore, you cant transfer to yourself. I took $10 from you for trying.")
        }

        if (ctx.args[0].toInt() > user.bankBalance) {
            return ctx.reply("wtf whore, you only have $${user.bankBalance} in your bank account")
        }

        val user2 = BoobBot.database.getUser(ctx.mentions[0].id)

        user.bankBalance -= ctx.args[0].toInt()
        user2.bankBalance += ctx.args[0].toInt()
        user.save()
        user2.save()

        ctx.reply("Transferred $${ctx.args[0]} to ${ctx.mentions[0].asTag}, You now have $${user.bankBalance} in the bank.")
    }
}
