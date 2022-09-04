package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "Tatted up women.", nsfw = true, category = Category.KINKS)
class Tattoo : BbApiCommand("tattoo")
