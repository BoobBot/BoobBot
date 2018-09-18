package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Play nice.", nsfw = true)
class Collared : BbApiCommand("collared")
