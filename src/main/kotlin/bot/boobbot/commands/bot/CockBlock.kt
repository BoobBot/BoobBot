package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.SubCommand

@CommandProperties(
    description = "Change whether you can receive dicks in your DMs",
    donorOnly = true,
    aliases = ["cb"]
)
class CockBlock : Command {
    override fun execute(ctx: MessageContext) {
        ctx.reply("`bbcockblock <${subcommands.keys.joinToString("|")}>`")
    }

    @SubCommand(aliases = ["On"])
    fun on(ctx: MessageContext) {
        BoobBot.database.setUserCockBlocked(ctx.user.id, true)
        ctx.reply("You're no longer able to receive dicks via DMs \uD83C\uDF46") // eggplant
    }

    @SubCommand(aliases = ["Off"])
    fun off(ctx: MessageContext) {
        BoobBot.database.setUserCockBlocked(ctx.user.id, false)
        ctx.reply("You're now able to receive dicks via DMs <:moans:583453348984913933>")
    }

}
