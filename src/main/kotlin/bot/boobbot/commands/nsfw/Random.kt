package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(description = "Random nsfw because why not.", nsfw = true, category = Category.GENERAL)
class Random : BbApiCommand("nsfw")
