package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(description = "Valentines ❤", nsfw = true, category = Category.HOLIDAY)
class VDay : BbApiCommand("vday")
