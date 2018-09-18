package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Everything is better with toys \uD83D\uDE09", nsfw = true, category = CommandProperties.category.KINKS)
class Toys : BbApiCommand("toys")
