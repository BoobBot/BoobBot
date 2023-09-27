package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.interfaces.Command

@CommandProperties(
    description = "Change whether you're shown on embeds",
    donorOnly = true,
    aliases = ["cb"],
    groupByCategory = true
)
class Anonymity : Command {
    override fun execute(ctx: Context) {
        ctx.reply("`bbanonymity <${subcommands.keys.joinToString("|")}>`")
    }

    @SubCommand(description = "Turn on anonymity.")
    fun on(ctx: Context) {
        BoobBot.database.setUserAnonymity(ctx.user.id, true)
        ctx.reply("You're now anonymous \uD83D\uDD75")
    }

    @SubCommand(description = "Turn off anonymity.")
    fun off(ctx: Context) {
        BoobBot.database.setUserAnonymity(ctx.user.id, false)
        ctx.reply("You're no longer anonymous 👀")
    }

}
