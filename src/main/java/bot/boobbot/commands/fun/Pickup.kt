package bot.boobbot.commands.`fun`

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.FunCommand

@CommandProperties(description = "Pickup someone.", category = CommandProperties.category.FUN, aliases = ["pu"])
class Pickup : FunCommand("pickups")
