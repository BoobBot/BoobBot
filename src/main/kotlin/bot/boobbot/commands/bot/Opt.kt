package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.SubCommand

@CommandProperties(description = "Changes whether you can receive nudes or not")
class Opt : Command {

    override fun execute(ctx: MessageContext) {
        ctx.reply("`bbopt <${subcommands.keys.joinToString("|")}>`")
    }

    @SubCommand(aliases = ["In"])
    fun `in`(ctx: MessageContext) {
        BoobBot.database.setUserCanReceiveNudes(ctx.user.id, true)
        ctx.reply("You're now able to receive nudes <:moans:583453348984913933>")
    }

    @SubCommand(aliases = ["Out"])
    fun out(ctx: MessageContext) {
        BoobBot.database.setUserCanReceiveNudes(ctx.user.id, false)
        ctx.reply("You can no longer receive nudes. Whore.")
    }

    @SubCommand(aliases = ["Status"])
    fun status(ctx: MessageContext) {
        val current = BoobBot.database.getCanUserReceiveNudes(ctx.user.id)
        val s = if (current) "can" else "can't"
        ctx.reply("You $s receive nudes.")
    }

}