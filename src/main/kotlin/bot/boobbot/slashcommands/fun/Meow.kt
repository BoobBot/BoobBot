package bot.boobbot.slashcommands.`fun`

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.NekoLifeApiSlashCommand

@CommandProperties(description = "random cat", category = Category.FUN, aliases = ["cat"])
class Meow : NekoLifeApiSlashCommand("meow")