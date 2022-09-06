package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.impl.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Fucking furries", nsfw = true, category = Category.FANTASY)
class Yiff : BbApiCommand("yiff")
