package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(description = "Everything is better with toys \uD83D\uDE09", nsfw = true, category = Category.KINKS)
class Toys : BbApiCommand("toys")
