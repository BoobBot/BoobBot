package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(description = "Boy love.", nsfw = true, category = Category.FANTASY)
class Yaoi : BbApiCommand("yaoi")
