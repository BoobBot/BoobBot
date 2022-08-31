package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties


@CommandProperties(description = "Gotta have that black love as well.", nsfw = true, category = Category.GENERAL)
class Black : BbApiSlashCommand("black")
