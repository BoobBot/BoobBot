package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Halloween \uD83D\uDC7B", nsfw = true, category = Category.HOLIDAY)
class Halloween : BbApiCommand("halloween")
