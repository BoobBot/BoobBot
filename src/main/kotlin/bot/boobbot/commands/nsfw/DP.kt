package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "Gotta get that double love!", nsfw = true, category = Category.GENERAL)
class DP : BbApiCommand("dpgirls")
