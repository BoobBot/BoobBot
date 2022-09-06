package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.impl.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Sexy gifs!", nsfw = true, category = Category.GENERAL)
class Gif : BbApiCommand("Gifs")

