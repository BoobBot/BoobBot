package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "For when 2 aren't enough...", nsfw = true)
class Group : BbApiCommand("group")
