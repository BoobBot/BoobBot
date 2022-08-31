package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiSlashCommand

@CommandProperties(description = "That ass love tho.", nsfw = true, category = Category.KINKS)
class Anal : BbApiSlashCommand("anal")
