package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "PornHub gifs! (Some are trash #BlamePornHub)", nsfw = true)
class PHGif : BbApiCommand("pGifs")
