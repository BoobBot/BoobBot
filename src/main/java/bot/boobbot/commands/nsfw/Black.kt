package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Gotta have that black love as well.", nsfw = true, category = CommandProperties.category.GENERAL)
class Black : BbApiCommand("black")
