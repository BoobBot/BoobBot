package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Random nsfw because why not.", nsfw = true, category = Category.GENERAL)
class Random : BbApiCommand("nsfw")
