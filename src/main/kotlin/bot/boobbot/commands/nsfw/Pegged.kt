package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.impl.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Strap-on love!", nsfw = true, category = Category.KINKS)
class Pegged : BbApiCommand("pegged")
