package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(description = "Gotta have that black love as well.", nsfw = true, category = Category.GENERAL)
class Black : BbApiCommand("black")
