package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.impl.BbApiInteractionCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "duh?", category = Category.INTERACTIONS, nsfw = true)
class `69` : BbApiInteractionCommand("69", "<:bunny69:866454901269069844> %s & %s 69")
