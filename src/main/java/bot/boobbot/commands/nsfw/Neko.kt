package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.NekoLifeApiCommand

@CommandProperties(description = "Lewd Nekos!", nsfw = true, category = Category.FANTASY)
class Neko : NekoLifeApiCommand("lewd")
