package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.interfaces.Command

@CommandProperties(description = "Changes whether you can receive nudes or not", groupByCategory = true)
class Opt : Command {

    override fun execute(ctx: Context) {
        ctx.reply("`bbopt <${subcommands.keys.joinToString("|")}>`")
    }

    @SubCommand(aliases = ["In"], description = "Enable receiving nudes in DMs (send* commands)")
    fun `in`(ctx: Context) {
        BoobBot.database.setUserCanReceiveNudes(ctx.user.id, true)
        ctx.reply("You're now able to receive nudes <:moans:583453348984913933>", true)
    }

    @SubCommand(aliases = ["Out"], description = "Disable receiving nudes in DMs (send* commands)")
    fun out(ctx: Context) {
        BoobBot.database.setUserCanReceiveNudes(ctx.user.id, false)
        ctx.reply("You can no longer receive nudes. Whore.", true)
    }

    @SubCommand(aliases = ["Status"], description = "View whether you can currently receive nudes.")
    fun status(ctx: Context) {
        val current = BoobBot.database.getCanUserReceiveNudes(ctx.user.id)
        val s = if (current) "can" else "can't"
        ctx.reply("You $s receive nudes.", true)
    }

}