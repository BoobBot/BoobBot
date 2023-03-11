package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(description = "Random thighs", nsfw = true, category = Category.KINKS)
class Thigh : BbApiCommand("ThighBot")
