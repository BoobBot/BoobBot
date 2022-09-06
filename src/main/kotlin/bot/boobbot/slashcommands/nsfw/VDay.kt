package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Valentines ❤", nsfw = true, category = Category.HOLIDAY)
class VDay : BbApiSlashCommand("vday")
