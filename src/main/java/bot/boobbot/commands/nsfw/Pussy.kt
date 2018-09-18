package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Pussy!", nsfw = true)
class Pussy : BbApiCommand("pussy")
