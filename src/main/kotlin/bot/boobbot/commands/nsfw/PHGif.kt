package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(
    description = "PornHub gifs! (Some are trash #BlamePornHub)",
    nsfw = true,
    category = Category.VIDEOSEARCHING
)
class PHGif : BbApiCommand("pGifs")
