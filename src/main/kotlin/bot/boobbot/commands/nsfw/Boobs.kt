package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.impl.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Shows some boobs.", nsfw = true, category = Category.GENERAL)
class Boobs : BbApiCommand("boobs")
