package bot.boobbot.slashcommands.`fun`

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.NekoLifeApiSlashCommand

@CommandProperties(description = "random dog", category = Category.FUN, aliases = ["woof"])
class Dog : NekoLifeApiSlashCommand("woof")
