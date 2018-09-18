package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Easter is nice \uD83D\uDC30", nsfw = true, category = CommandProperties.category.HOLIDAY)
class Easter : BbApiCommand("easter")
