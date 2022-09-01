package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.*

@CommandProperties(description = "Tease someone.", category = Category.INTERACTIONS, nsfw = true)
class Tease : BbApiInteractionSlashCommand("tease", "<a:tease:866433430744596510> %s teases %s")
