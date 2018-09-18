package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "PornHub gifs! (Some are trash #BlamePornHub)", nsfw = true, category = CommandProperties.category.VIDEOSEARCHING)
class PHGif : BbApiCommand("pGifs")
