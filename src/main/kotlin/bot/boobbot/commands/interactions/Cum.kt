package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.impl.BbApiInteractionCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "cum on someone", category = Category.INTERACTIONS, nsfw = true)
class Cum : BbApiInteractionCommand("cum", "<a:came:501483987370377226> %s came on %s, oh my")
