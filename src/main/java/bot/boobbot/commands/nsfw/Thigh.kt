package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Random thighs", nsfw = true)
class Thigh : BbApiCommand("ThighBot")
