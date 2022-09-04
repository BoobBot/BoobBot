package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.BbApiInteractionSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "Suck someone.", category = Category.INTERACTIONS, nsfw = true)
class Suck : BbApiInteractionSlashCommand("suck", "<a:nekosuck:501483984136699904> %s Sucks off %s")
