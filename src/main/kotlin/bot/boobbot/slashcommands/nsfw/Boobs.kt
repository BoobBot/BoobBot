package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Shows some boobs.", nsfw = true, category = Category.GENERAL)
class Boobs : BbApiSlashCommand("boobs")