package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Play nice.", nsfw = true, category = CommandProperties.category.KINKS)
class Collared : BbApiCommand("collared")
