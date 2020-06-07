package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(description = "Pokemon Porn!", nsfw = true, category = Category.FANTASY)
class Poke : BbApiCommand("PokePorn")
