package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "PornHub gifs! (Some are trash #BlamePornHub)", nsfw = true, category = Category.VIDEOSEARCHING)
class PHGif : BbApiCommand("pGifs")
