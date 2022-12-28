package bot.boobbot.commands.`fun`

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.NekoLifeApiCommand

@CommandProperties(description = "random cat", category = Category.FUN, aliases = ["meow"], groupByCategory = true)
class Cat : NekoLifeApiCommand("meow")
