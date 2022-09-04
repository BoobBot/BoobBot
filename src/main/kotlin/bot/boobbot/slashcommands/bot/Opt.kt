package bot.boobbot.slashcommands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext

@CommandProperties(description = "Changes whether you can receive nudes or not")
class Opt : SlashCommand {
    override fun execute(ctx: SlashContext) {
        when (ctx.subcommandName) {
            "in" -> `in`(ctx)
            "out" -> out(ctx)
            "status" -> status(ctx)
            else -> ctx.reply("Unknown subcommand")
        }
    }

    fun `in`(ctx: SlashContext) {
        BoobBot.database.setUserCanReceiveNudes(ctx.user.id, true)
        ctx.reply("You're now able to receive nudes <:moans:583453348984913933>")
    }

    fun out(ctx: SlashContext) {
        BoobBot.database.setUserCanReceiveNudes(ctx.user.id, false)
        ctx.reply("You can no longer receive nudes. Whore.")
    }

    fun status(ctx: SlashContext) {
        val current = BoobBot.database.getCanUserReceiveNudes(ctx.user.id)
        val s = if (current) "can" else "can't"
        ctx.reply("You $s receive nudes.")
    }

}
