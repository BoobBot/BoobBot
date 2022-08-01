package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.*

@CommandProperties(description = "Suck someone.", category = Category.INTERACTIONS, nsfw = true)
class Suck : BbApiInteractionCommand("suck", "<a:nekosuck:501483984136699904> %s Sucks off %s")
