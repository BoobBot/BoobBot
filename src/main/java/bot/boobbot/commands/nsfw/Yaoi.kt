package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Boy love.", nsfw = true)
class Yaoi : BbApiCommand("yaoi")
