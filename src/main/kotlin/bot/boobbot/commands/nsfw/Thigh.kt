package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Random thighs", nsfw = true, category = Category.KINKS)
class Thigh : BbApiCommand("ThighBot")
