package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.impl.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "The beautiful thicc and chubby.", nsfw = true, category = Category.GENERAL)
class Thicc : BbApiCommand("thicc")
