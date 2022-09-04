package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.BbApiInteractionSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "duh?", category = Category.INTERACTIONS, nsfw = true)
class `69` : BbApiInteractionSlashCommand("69", "<:bunny69:866454901269069844> %s & %s 69")
