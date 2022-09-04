package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.BbApiInteractionSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "Tease someone.", category = Category.INTERACTIONS, nsfw = true)
class Tease : BbApiInteractionSlashCommand("tease", "<a:tease:866433430744596510> %s teases %s")
