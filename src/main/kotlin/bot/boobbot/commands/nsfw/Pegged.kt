package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "Strap-on love!", nsfw = true, category = Category.KINKS)
class Pegged : BbApiCommand("pegged")
