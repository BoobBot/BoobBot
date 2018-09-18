package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Lesbians are sexy!", nsfw = true, category = CommandProperties.category.KINKS)
class Lesbians : BbApiCommand("lesbians")
