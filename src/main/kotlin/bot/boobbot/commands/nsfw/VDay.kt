package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(description = "Valentines ❤", nsfw = true, category = Category.HOLIDAY)
class VDay : BbApiCommand("vday")
