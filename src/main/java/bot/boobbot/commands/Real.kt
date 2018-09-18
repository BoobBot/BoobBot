package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Real girls!", nsfw = true)
class Real : BbApiCommand("real")
