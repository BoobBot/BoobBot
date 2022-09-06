package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Boy love.", nsfw = true, category = Category.FANTASY)
class Yaoi : BbApiSlashCommand("yaoi")
