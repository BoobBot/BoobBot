package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.*

@CommandProperties(description = "dom someone", category = Category.INTERACTIONS, nsfw = true)
class Dom : BbApiInteractionCommand("dom", "<:dom:866457723788329020> %s dominates %s")
