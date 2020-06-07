package bot.boobbot.commands.`fun`

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.FunCommand

@CommandProperties(description = "fun interactions.", category = Category.FUN, aliases = ["int"])
class Interact : FunCommand("interaction")
