package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "Shows some ass.", nsfw = true, category = Category.GENERAL)
class Ass : BbApiSlashCommand("ass")
