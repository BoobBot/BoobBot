package bot.boobbot.commands._fun

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.FunCommand

@CommandProperties(description = "Insult someone.", nsfw = false, category = CommandProperties.category.FUN, aliases = ["pu"])
class Insult : FunCommand("insult")
