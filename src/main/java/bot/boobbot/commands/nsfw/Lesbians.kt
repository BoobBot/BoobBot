package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Lesbians are sexy!", nsfw = true, category = Category.KINKS)
class Lesbians : BbApiCommand("lesbians")
