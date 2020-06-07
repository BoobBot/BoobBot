package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(description = "That ass love tho.", nsfw = true, category = Category.KINKS)
class Anal : BbApiCommand("anal")
