package bot.boobbot.commands._fun

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.FunCommand

@CommandProperties(description = "Pickup someone.", nsfw = false, category = CommandProperties.category.FUN, aliases = ["pu"])
class Pickup : FunCommand("pickups")
