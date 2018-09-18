package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Tatted up women.", nsfw = true, category = CommandProperties.category.KINKS)
class Tattoo : BbApiCommand("wtats")
