package bot.boobbot.contextual.user

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.FunUserContextCommand

@CommandProperties(description = "hug interactions.", category = Category.FUN)
class Hug : FunUserContextCommand("hugs")
