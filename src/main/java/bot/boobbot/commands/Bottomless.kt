package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Sexy!", nsfw = true)
class Bottomless : BbApiCommand("bottomless")
