package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.FunCommand
import bot.boobbot.entities.framework.FunSlashCommand

@CommandProperties(description = "Kill someone.", category = Category.INTERACTIONS)
class Kill : FunSlashCommand("kills")
