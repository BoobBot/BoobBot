package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.interfaces.Command

@CommandProperties(
    description = "Change whether you can receive dicks in your DMs",
    donorOnly = true,
    aliases = ["cb"],
    groupByCategory = true
)
class CockBlock : Command {
    override fun execute(ctx: Context) {
        sendSubcommandHelp(ctx)
    }

    @SubCommand(aliases = ["On"], description = "Disable receiving dicks in DMs.")
    fun on(ctx: Context) {
        BoobBot.database.setUserCockBlocked(ctx.user.id, true)
        ctx.reply("You're no longer able to receive dicks via DMs \uD83C\uDF46") // eggplant
    }

    @SubCommand(aliases = ["Off"], description = "Enable receiving dicks in DMs.")
    fun off(ctx: Context) {
        BoobBot.database.setUserCockBlocked(ctx.user.id, false)
        ctx.reply("You're now able to receive dicks via DMs <:moans:583453348984913933>")
    }
}
