package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(description = "Pokemon Porn!", nsfw = true, category = Category.FANTASY)
class Poke : BbApiCommand("PokePorn")
