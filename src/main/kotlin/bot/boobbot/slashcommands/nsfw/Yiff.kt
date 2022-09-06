package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Fucking furries", nsfw = true, category = Category.FANTASY)
class Yiff : BbApiSlashCommand("yiff")
