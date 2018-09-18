package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Play nice.", nsfw = true)
class Collared : BbApiCommand("Collared")
