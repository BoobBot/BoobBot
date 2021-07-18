package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.FunCommand

@CommandProperties(description = "Kill someone.", category = Category.FUN)
class Kill : FunCommand("kills")
