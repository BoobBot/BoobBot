package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.FunCommand

@CommandProperties(description = "Pickup someone.", category = Category.FUN, aliases = ["pu"])
class Pickup : FunCommand("pickups")
