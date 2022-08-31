package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiSlashCommand

@CommandProperties(description = "For when 2 aren't enough...", nsfw = true, category = Category.KINKS)
class Group : BbApiSlashCommand("group")
