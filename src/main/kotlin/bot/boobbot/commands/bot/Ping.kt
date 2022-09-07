package bot.boobbot.commands.bot

import bot.boobbot.entities.framework.*
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.interfaces.Command

@CommandProperties(description = "Pong!", category = Category.MISC)
class Ping : Command {
    override fun execute(ctx: Context) {
        ctx.reply("What do you want me to say, pong? No you can go fuck yourself~")
    }
}