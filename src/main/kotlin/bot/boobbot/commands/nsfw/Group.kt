package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(description = "For when 2 aren't enough...", nsfw = true, category = Category.KINKS)
class Group : BbApiCommand("group")
