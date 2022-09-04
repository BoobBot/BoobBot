package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "The beautiful thicc and chubby.", nsfw = true, category = Category.GENERAL)
class Thicc : BbApiSlashCommand("thicc")
