package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(description = "Shows some tummy.", nsfw = true, category = Category.GENERAL)
class Tummy : BbApiCommand("tummy")
