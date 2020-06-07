package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlideShowCommand

@CommandProperties(
    description = "Cycles though 20 images at 5 seconds each. <:p_:475801484282429450>",
    nsfw = true,
    category = Category.GENERAL,
    aliases = ["ss"],
    donorOnly = true
)
class SlideShow : SlideShowCommand()
