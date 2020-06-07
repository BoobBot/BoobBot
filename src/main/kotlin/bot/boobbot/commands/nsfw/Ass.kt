package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(description = "Shows some ass.", nsfw = true, category = Category.GENERAL)
class Ass : BbApiCommand("ass")
