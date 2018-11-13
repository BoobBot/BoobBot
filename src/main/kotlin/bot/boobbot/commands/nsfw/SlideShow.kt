package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.SlideShowCommand

@CommandProperties(
    description = "Cycles though 20 images at 5 seconds each. <:p_:475801484282429450>",
    nsfw = true,
    category = Category.GENERAL,
    aliases = ["ss"],
    donorOnly = true
)
class SlideShow : SlideShowCommand()
