package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Command
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.SubCommand

@CommandProperties(
    description = "Change whether you're shown on embeds",
    donorOnly = true,
    aliases = ["cb"]
)
class Anonymity : Command {
    override fun execute(ctx: Context) {
        ctx.send("`bbanonymity <${subcommands.keys.joinToString("|")}>`")
    }

    @SubCommand(aliases = ["On"])
    fun on(ctx: Context) {
        BoobBot.database.setUserAnonymity(ctx.author.id, true)
        ctx.send("You're now anonymous \uD83D\uDD75")
    }

    @SubCommand(aliases = ["Off"])
    fun off(ctx: Context) {
        BoobBot.database.setUserAnonymity(ctx.author.id, false)
        ctx.send("You're no longer anonymous 👀")
    }

}
