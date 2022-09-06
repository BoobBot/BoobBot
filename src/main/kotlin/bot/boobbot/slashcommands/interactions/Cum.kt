package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.BbApiInteractionSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "cum on someone", category = Category.INTERACTIONS, nsfw = true)
class Cum : BbApiInteractionSlashCommand("cum", "<a:came:501483987370377226> %s came on %s, oh my")
