package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.BbApiInteractionSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Fuck someone.", category = Category.INTERACTIONS, aliases = ["bang"], nsfw = true)
class Fuck : BbApiInteractionSlashCommand("fuck", "<:bunnyfuck:505072924449964053> %s fucks %s")
