package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.NekoLifeApiCommand

@CommandProperties(
    description = "Lewd hentai gifs! <:p_:475801484282429450>",
    nsfw = true,
    category = Category.FANTASY,
    donorOnly = true,
    aliases = ["hg"]
)
class HentaiGif : NekoLifeApiCommand("Random_hentai_gif")
