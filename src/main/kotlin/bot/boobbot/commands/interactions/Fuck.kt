package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.BbApiInteractionCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "Fuck someone.", category = Category.FUN, aliases = ["bang"], nsfw = true)
class Fuck : BbApiInteractionCommand("fuck", "<:bunnyfuck:505072924449964053> %s fucks %s")
