package bot.boobbot.commands.bot

import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context

@CommandProperties(description = "help, --dm for dm", aliases = ["halp", "halllp", "coms", "commands"], category = CommandProperties.category.MISC)
class Help : Command {

    override fun execute(ctx: Context) {

    }

}