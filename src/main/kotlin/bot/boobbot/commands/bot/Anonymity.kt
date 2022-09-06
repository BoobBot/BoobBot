package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.SubCommand

@CommandProperties(
    description = "Change whether you're shown on embeds",
    donorOnly = true,
    aliases = ["cb"]
)
class Anonymity : Command {
    override fun execute(ctx: MessageContext) {
        ctx.reply("`bbanonymity <${subcommands.keys.joinToString("|")}>`")
    }

    @SubCommand(aliases = ["On"])
    fun on(ctx: MessageContext) {
        BoobBot.database.setUserAnonymity(ctx.user.id, true)
        ctx.reply("You're now anonymous \uD83D\uDD75")
    }

    @SubCommand(aliases = ["Off"])
    fun off(ctx: MessageContext) {
        BoobBot.database.setUserAnonymity(ctx.user.id, false)
        ctx.reply("You're no longer anonymous 👀")
    }

}
