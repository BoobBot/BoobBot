package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Sexy!", nsfw = true)
class Bottomless : BbApiCommand("bottomless")
