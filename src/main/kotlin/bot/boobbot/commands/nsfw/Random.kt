package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.impl.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Random nsfw because why not.", nsfw = true, category = Category.GENERAL)
class Random : BbApiCommand("nsfw")
