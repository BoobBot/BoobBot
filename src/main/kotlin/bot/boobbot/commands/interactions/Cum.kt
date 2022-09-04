package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.BbApiInteractionCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "cum on someone", category = Category.INTERACTIONS, nsfw = true)
class Cum : BbApiInteractionCommand("cum", "<a:came:501483987370377226> %s came on %s, oh my")
