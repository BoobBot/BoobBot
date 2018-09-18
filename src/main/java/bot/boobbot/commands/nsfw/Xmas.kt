package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Christmas \uD83C\uDF85", nsfw = true, category = CommandProperties.category.HOLIDAY)
class Xmas : BbApiCommand("xmas")
