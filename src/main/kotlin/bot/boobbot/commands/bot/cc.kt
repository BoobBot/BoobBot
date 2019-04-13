package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.flight.SubCommand

@CommandProperties(description = "custom commands")
class cc : Command {

    override fun execute(ctx: Context) {
        ctx.send("`bbcc <${subcommands.keys.joinToString("|")}>`")
    }

    @SubCommand
    fun add(ctx: Context) {
        BoobBot.database.addCustomCommand(ctx.guild!!.id, ctx.args[0],  ctx.args.sliceArray(1 until ctx.args.size).joinToString(" "))
        ctx.send("done whore")
    }

    @SubCommand
    fun del(ctx: Context) {
        ctx.send(".")
    }

    @SubCommand
    fun list(ctx: Context) {

        ctx.send(".")
    }

}