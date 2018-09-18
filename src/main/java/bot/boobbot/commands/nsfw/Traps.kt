package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Traps are hot!", nsfw = true)
class Traps : BbApiCommand("traps")
