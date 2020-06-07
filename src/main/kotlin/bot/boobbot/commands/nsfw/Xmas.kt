package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(description = "Christmas \uD83C\uDF85", nsfw = true, category = Category.HOLIDAY)
class Xmas : BbApiCommand("xmas")
