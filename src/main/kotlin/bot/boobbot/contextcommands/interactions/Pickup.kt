package bot.boobbot.contextcommands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.FunUserContextCommand

@CommandProperties(description = "fun interactions.", category = Category.FUN, aliases = ["int"])
class Pickup : FunUserContextCommand("pickups")
