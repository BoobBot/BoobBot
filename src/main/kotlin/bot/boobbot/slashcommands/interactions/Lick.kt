package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.BbApiInteractionSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "lick someone.", category = Category.INTERACTIONS, nsfw = true)
class Lick : BbApiInteractionSlashCommand("lick", "<a:play:866441014830563388> %s licks %s")
