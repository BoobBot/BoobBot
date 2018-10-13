package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Shows some boobs.", nsfw = true, category = Category.GENERAL)
class Boobs : BbApiCommand("boobs")
