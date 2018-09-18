package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "For when 2 aren't enough...", nsfw = true, category = CommandProperties.category.KINKS)
class Group : BbApiCommand("group")
