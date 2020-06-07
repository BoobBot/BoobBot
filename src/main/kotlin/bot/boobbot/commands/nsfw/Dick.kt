package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(description = "Got dick?", nsfw = true, category = Category.GENERAL)
class Dick : BbApiCommand("penis")
