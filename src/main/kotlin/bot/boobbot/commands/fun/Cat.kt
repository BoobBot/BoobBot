package bot.boobbot.commands.`fun`

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.NekoLifeApiCommand

@CommandProperties(description = "random cat", category = Category.FUN, aliases = ["meow"])
class Cat : NekoLifeApiCommand("meow")
