package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.impl.BbApiInteractionCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Tease someone.", category = Category.INTERACTIONS, nsfw = true)
class Tease : BbApiInteractionCommand("tease", "<a:tease:866433430744596510> %s teases %s")
