package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Hentai Traps.", nsfw = true, category = CommandProperties.category.FANTASY)
class Futa : BbApiCommand("futa")
