package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.NekoLifeApiCommand

@CommandProperties(description = "Lewd hentai gifs! <:p_:475801484282429450>", nsfw = true, category = CommandProperties.category.FANTASY, donorOnly = true, aliases = ["hg"])
class HentaiGif : NekoLifeApiCommand("Random_hentai_gif")
