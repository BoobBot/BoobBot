package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Hentai.", nsfw = true)
class Hentai : BbApiCommand("hentai")
