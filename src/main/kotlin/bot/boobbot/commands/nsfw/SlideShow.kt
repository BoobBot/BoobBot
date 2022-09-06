package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.SlideShowCommand

@CommandProperties(
    description = "Cycles though 20 images at 5 seconds each.",
    nsfw = true,
    category = Category.GENERAL,
    aliases = ["ss"],
    donorOnly = true
)
class SlideShow : SlideShowCommand()
