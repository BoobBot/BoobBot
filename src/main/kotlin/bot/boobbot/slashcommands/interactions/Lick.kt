package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.*

@CommandProperties(description = "lick someone.", category = Category.INTERACTIONS, nsfw = true)
class Lick : BbApiInteractionSlashCommand("lick", "<a:play:866441014830563388> %s licks %s")
