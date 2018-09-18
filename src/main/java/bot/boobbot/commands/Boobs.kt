package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Shows some boobs.", nsfw = true)
class Boobs : BbApiCommand("boobs")
