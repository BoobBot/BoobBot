package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.FunCommand

@CommandProperties(description = "Insult someone.", category = Category.FUN, aliases = ["ins"])
class Insult : FunCommand("insult")
