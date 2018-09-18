package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Got men?", nsfw = true)
class Gay : BbApiCommand("gay")
