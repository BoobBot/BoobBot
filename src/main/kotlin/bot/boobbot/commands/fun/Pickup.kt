package bot.boobbot.commands.`fun`

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.FunCommand

@CommandProperties(description = "Pickup someone.", category = Category.FUN, aliases = ["pu"])
class Pickup : FunCommand("pickups")
