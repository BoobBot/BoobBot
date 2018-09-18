package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Shows some ass.", nsfw = true, category = CommandProperties.category.GENERAL)
class Ass : BbApiCommand("ass")
