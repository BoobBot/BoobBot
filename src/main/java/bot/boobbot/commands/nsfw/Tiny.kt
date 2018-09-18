package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "Tiny girls!", nsfw = true, category = CommandProperties.category.KINKS)
class Tiny : BbApiCommand("tiny")
