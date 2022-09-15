package bot.boobbot.contextcommands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.FunUserContextCommand

@CommandProperties(description = "hug interactions.", category = Category.FUN, aliases = [])
class Hug : FunUserContextCommand("hugs")
