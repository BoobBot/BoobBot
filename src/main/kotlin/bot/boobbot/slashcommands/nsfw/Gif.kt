package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Sexy gifs!", nsfw = true, category = Category.GENERAL)
class Gif : BbApiSlashCommand("Gifs")
