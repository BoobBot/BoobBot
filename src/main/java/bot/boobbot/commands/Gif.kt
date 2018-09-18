package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Sexy gifs!", nsfw = true)
class Gif : BbApiCommand("Gifs")
