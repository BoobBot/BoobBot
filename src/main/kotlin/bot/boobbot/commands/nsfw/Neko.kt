package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.NekoLifeApiCommand

@CommandProperties(description = "Lewd Nekos!", nsfw = true, category = Category.FANTASY)
class Neko : NekoLifeApiCommand("lewd")
