package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Strap-on love!", nsfw = true, category = CommandProperties.category.KINKS)
class Pegged : BbApiCommand("pegged")
