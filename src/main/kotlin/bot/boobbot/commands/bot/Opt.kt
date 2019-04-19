package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.flight.SubCommand

@CommandProperties(description = "Changes whether you can receive nudes or not")
class Opt : Command {

    override fun execute(ctx: Context) {
        ctx.send("`bbopt <${subcommands.keys.joinToString("|")}>`")
    }

    @SubCommand(aliases = ["In"])
    fun `in`(ctx: Context) {
        BoobBot.database.setUserCanReceiveNudes(ctx.author.id, true)
        ctx.send("You're now able to receive nudes :thumbsup:")
    }

    @SubCommand(aliases = ["Out"])
    fun out(ctx: Context) {
        BoobBot.database.setUserCanReceiveNudes(ctx.author.id, false)
        ctx.send("You can no longer receive nudes. Whore.")
    }

    @SubCommand(aliases = ["Status"])
    fun status(ctx: Context) {
        val current = BoobBot.database.getCanUserReceiveNudes(ctx.author.id)
        val s = if (current) "can" else "can't"
        ctx.send("You $s receive nudes.")
    }

}