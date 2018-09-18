package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Valentines ❤", nsfw = true)
class VDay : BbApiCommand("vday")
