package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Hentai.", nsfw = true, category = Category.FANTASY)
class Hentai : BbApiCommand("hentai")
