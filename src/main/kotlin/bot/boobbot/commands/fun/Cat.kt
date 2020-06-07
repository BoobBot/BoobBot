package bot.boobbot.commands.`fun`

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.NekoLifeApiCommand

@CommandProperties(description = "random cat", category = Category.FUN, aliases = ["meow"])
class Cat : NekoLifeApiCommand("meow")
