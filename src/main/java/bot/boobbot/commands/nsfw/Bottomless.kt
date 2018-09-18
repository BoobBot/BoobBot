package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Sexy!", nsfw = true, category = CommandProperties.category.KINKS)
class Bottomless : BbApiCommand("bottomless")
