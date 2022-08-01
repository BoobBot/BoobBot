package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.*

@CommandProperties(description = "Are you extreme?", category = Category.INTERACTIONS, nsfw = true)
class Extreme : BbApiInteractionCommand("extreme", "<:extreme:866451714471231510> %s likes it extreme. Are you in %s?")
