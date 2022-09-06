package bot.boobbot.slashcommands.interactions

import bot.boobbot.entities.framework.BbApiInteractionSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "finger someone.", category = Category.INTERACTIONS, nsfw = true)
class Finger : BbApiInteractionSlashCommand("finger", "<a:lemmeegirlu:761359677778821130> %s fingers %s, and they like it!")
