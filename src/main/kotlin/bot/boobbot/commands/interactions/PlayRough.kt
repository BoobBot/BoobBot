package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.*

@CommandProperties(description = "Play rough with someone.", category = Category.INTERACTIONS, nsfw = true)
class PlayRough : BbApiInteractionCommand("playrough", "<a:play:866441014830563388> %s plays rough with %s")
