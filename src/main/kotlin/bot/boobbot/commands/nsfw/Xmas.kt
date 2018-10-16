package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Christmas \uD83C\uDF85", nsfw = true, category = Category.HOLIDAY)
class Xmas : BbApiCommand("xmas")
