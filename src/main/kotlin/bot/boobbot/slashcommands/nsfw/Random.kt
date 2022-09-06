package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Random nsfw because why not.", nsfw = true, category = Category.GENERAL)
class Random : BbApiSlashCommand("nsfw")
