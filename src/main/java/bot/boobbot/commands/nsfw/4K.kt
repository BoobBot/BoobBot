package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "4K Hotness!", nsfw = true, category = CommandProperties.category.GENERAL)
class `4K` : BbApiCommand("4k")
