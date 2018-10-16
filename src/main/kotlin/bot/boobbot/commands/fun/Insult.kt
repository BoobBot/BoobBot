package bot.boobbot.commands.`fun`

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.FunCommand

@CommandProperties(description = "Insult someone.", category = Category.FUN, aliases = ["pu"])
class Insult : FunCommand("insult")
