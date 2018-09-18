package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Pokemon Porn!", nsfw = true)
class Poke : BbApiCommand("PokePorn")
