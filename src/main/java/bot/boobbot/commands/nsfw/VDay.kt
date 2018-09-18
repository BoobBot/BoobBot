package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Valentines ❤", nsfw = true, category = CommandProperties.category.HOLIDAY)
class VDay : BbApiCommand("vday")
