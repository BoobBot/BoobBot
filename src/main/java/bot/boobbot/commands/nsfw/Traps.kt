package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Traps are hot!", nsfw = true, category = CommandProperties.category.KINKS)
class Traps : BbApiCommand("traps")
