package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.BbApiInteractionSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "dom someone", category = Category.INTERACTIONS, nsfw = true)
class Dom : BbApiInteractionSlashCommand("dom", "<:dom:866457723788329020> %s dominates %s")
