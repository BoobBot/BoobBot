package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.NekoLifeApiCommand

@CommandProperties(description = "Lewd Nekos!", nsfw = true, category = CommandProperties.category.FANTASY)
class Neko : NekoLifeApiCommand("lewd")
