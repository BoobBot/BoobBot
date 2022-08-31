package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiSlashCommand

@CommandProperties(description = "Everything is better with toys \uD83D\uDE09", nsfw = true, category = Category.KINKS)
class Toys : BbApiSlashCommand("toys")
