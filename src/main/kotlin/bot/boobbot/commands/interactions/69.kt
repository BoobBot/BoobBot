package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.BbApiInteractionCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "duh?", category = Category.INTERACTIONS, nsfw = true)
class `69` : BbApiInteractionCommand("69", "<:bunny69:866454901269069844> %s & %s 69")
