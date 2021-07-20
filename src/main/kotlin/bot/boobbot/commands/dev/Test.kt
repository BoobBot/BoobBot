package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.User
import bot.boobbot.entities.framework.*


@CommandProperties()
class Test : Command {

    override fun execute(ctx: Context) {
        sendSubcommandHelp(ctx)
    }
}


