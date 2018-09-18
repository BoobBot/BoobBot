package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Christmas \uD83C\uDF85", nsfw = true)
class Xmas : BbApiCommand("xmas")
