package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(description = "Gotta get that double love!", nsfw = true, category = Category.GENERAL)
class DP : BbApiCommand("dpgirls")
