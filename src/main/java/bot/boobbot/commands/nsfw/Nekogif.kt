package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.NekoLifeApiCommand

@CommandProperties(description = "Lewd Neko gifs! <:p_:475801484282429450>", nsfw = true, category = CommandProperties.category.FANTASY, donorOnly = true, aliases = ["ng"])
class Nekogif : NekoLifeApiCommand("nsfw_neko_gif")
