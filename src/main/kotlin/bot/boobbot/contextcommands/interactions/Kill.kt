package bot.boobbot.contextcommands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.FunUserContextCommand

@CommandProperties(description = "fun interactions.", category = Category.FUN, aliases = ["int"])
class Kill : FunUserContextCommand("kills")
