package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Sexy!", nsfw = true, category = Category.KINKS)
class Bottomless : BbApiCommand("bottomless")
