package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.impl.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Valentines ❤", nsfw = true, category = Category.HOLIDAY)
class VDay : BbApiCommand("vday")
