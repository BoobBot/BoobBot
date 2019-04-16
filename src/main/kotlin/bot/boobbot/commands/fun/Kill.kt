package bot.boobbot.commands.`fun`

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.FunCommand

@CommandProperties(description = "Kill someone.", category = Category.FUN)
class Kill : FunCommand("kills")
