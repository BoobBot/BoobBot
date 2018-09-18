package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Gotta get that double love!", nsfw = true)
class DP : BbApiCommand("dpgirls")
