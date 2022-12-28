package bot.boobbot.commands.`fun`

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.NekoLifeApiCommand

@CommandProperties(description = "random Lizard", category = Category.FUN, groupByCategory = true)
class Lizard : NekoLifeApiCommand("lizard")
