package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.FunSlashCommand

@CommandProperties(description = "fun interactions.", category = Category.INTERACTIONS, aliases = ["int"])
class Interact : FunSlashCommand("interaction")
