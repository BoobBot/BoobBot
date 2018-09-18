package bot.boobbot.commands

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Easter is nice \uD83D\uDC30", nsfw = true)
class Easter : BbApiCommand("easter")
