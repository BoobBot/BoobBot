package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.*

@CommandProperties(description = "Play rough with someone.", category = Category.INTERACTIONS, nsfw = true)
class PlayRough : BbApiInteractionSlashCommand("playrough", "<a:play:866441014830563388> %s plays rough with %s")
