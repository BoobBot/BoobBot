package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.User
import bot.boobbot.entities.framework.*
import java.security.Permission


@CommandProperties(aliases = ["bank"], description = "banking operations \uD83C\uDFE6", guildOnly = true, category = Category.ECONOMY)
class bank : Command {

    override fun execute(ctx: Context) {
        sendSubcommandHelp(ctx)
    }

    @SubCommand(aliases = ["dep"], description = "deposit funds. \t\uD83D\uDCB0")
    fun deposit(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify how much to deposit, whore.")
        }

        if (ctx.args[0].toIntOrNull() == null) {
            return ctx.send("wtf whore, thats not a number")
        }
        val user: User by lazy { BoobBot.database.getUser(ctx.author.id) }

        if (ctx.args[0].toInt() < 0){
            return ctx.send("wtf are you doing?")
        }

        if (ctx.args[0].toInt() > user.balance) {
            return ctx.send("wtf whore, you only have ${user.balance}")
        }

        user.balance -= ctx.args[0].toInt()
        user.bankBalance += ctx.args[0].toInt()
        user.save()

        ctx.send("Deposited  $${ctx.args[0]}, You now have $${user.bankBalance} in the bank.")
    }

    @SubCommand(aliases = ["with"], description = "withdraw funds. \uD83D\uDCB8")
    fun withdraw(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify how much to withdraw, whore.")
        }

        if (ctx.args[0].toIntOrNull() == null) {
            return ctx.send("wtf whore, thats not a number")
        }
        val user: User by lazy { BoobBot.database.getUser(ctx.author.id) }

        if (ctx.args[0].toInt() < 0){
            return ctx.send("wtf are you doing?")
        }

        if (ctx.args[0].toInt() > user.bankBalance) {
            return ctx.send("wtf whore, you only have ${user.bankBalance}")
        }

        user.bankBalance -= ctx.args[0].toInt()
        user.balance += ctx.args[0].toInt()
        user.save()

        ctx.send("Withdrew  $${ctx.args[0]}, You now have $${user.bankBalance} in the bank.")
    }

    @SubCommand(aliases = ["bal"], description = "check your funds. \uD83D\uDCB3")
    fun balance(ctx: Context) {
        val user: User by lazy { BoobBot.database.getUser(ctx.author.id) }
        ctx.send("You are carrying $${user.balance} and have $${user.bankBalance} in the bank.")
    }

    @SubCommand(aliases = ["trans"], description = "transfer funds. ⇆")
    fun transfer(ctx: Context) {
        if (ctx.mentions.isEmpty()) {
            return ctx.send("How in the fuck would i know who you want to transfer to if you don't give me a valid mention? `try: bbbank transfer 500 @user#0000`")
        }

        if (ctx.args[0].isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify how much to deposit, whore.")
        }

        if (ctx.args[0].toInt() < 0){
            return ctx.send("wtf are you doing?")
        }

        if (ctx.args[0].toIntOrNull() == null) {
            return ctx.send("wtf whore, that's not a number. `try: bbbank transfer 500 @user#0000`")
        }

        val user: User by lazy { BoobBot.database.getUser(ctx.author.id) }

        if (ctx.mentions[0].id == ctx.author.id) {
            user.bankBalance -= 10
            user.save()
            return ctx.send("Don't be a whore, you cant transfer to yourself. I took $10 from you for trying.")
        }

        if (ctx.args[0].toInt() > user.bankBalance) {
            return ctx.send("wtf whore, you only have $${user.bankBalance} in your bank account")
        }

        val user2: User by lazy { BoobBot.database.getUser(ctx.mentions[0].id) }

        user.bankBalance -= ctx.args[0].toInt()
        user2.bankBalance += ctx.args[0].toInt()
        user.save()
        user2.save()

        ctx.send("Transferred $${ctx.args[0]} to ${ctx.mentions[0].asTag}, You now have $${user.bankBalance} in the bank.")
    }


}


