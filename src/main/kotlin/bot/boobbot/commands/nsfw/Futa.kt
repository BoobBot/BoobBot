package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Hentai Traps.", nsfw = true, category = Category.FANTASY)
class Futa : BbApiCommand("futa")
