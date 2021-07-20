package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.FunSlashCommand

@CommandProperties(description = "bot.boobbot.slashcommands.interactions.Pickup someone.", category = Category.FUN, aliases = ["pu"])
class Pickup : FunSlashCommand("pickups")
