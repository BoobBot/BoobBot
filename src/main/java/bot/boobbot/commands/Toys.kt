package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Everything is better with toys \uD83D\uDE09", nsfw = true)
class Toys : BbApiCommand("toys")
