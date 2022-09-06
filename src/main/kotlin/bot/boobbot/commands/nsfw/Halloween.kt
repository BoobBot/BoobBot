package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.impl.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Halloween \uD83D\uDC7B", nsfw = true, category = Category.HOLIDAY)
class Halloween : BbApiCommand("halloween")
