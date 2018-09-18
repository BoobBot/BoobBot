package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Real girls!", nsfw = true, category = CommandProperties.category.GENERAL)
class Real : BbApiCommand("real")
