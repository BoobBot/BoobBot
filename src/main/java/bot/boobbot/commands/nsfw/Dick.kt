package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Got dick?", nsfw = true, category = CommandProperties.category.GENERAL)
class Dick : BbApiCommand("penis")
