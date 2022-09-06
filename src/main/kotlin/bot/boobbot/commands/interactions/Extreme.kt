package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.impl.BbApiInteractionCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Are you extreme?", category = Category.INTERACTIONS, nsfw = true)
class Extreme : BbApiInteractionCommand("extreme", "<:extreme:866451714471231510> %s likes it extreme. Are you in %s?")
