package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Gotta get that double love!", nsfw = true, category = CommandProperties.category.GENERAL)
class DP : BbApiCommand("dpgirls")
