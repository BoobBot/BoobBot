package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Halloween \uD83D\uDC7B", nsfw = true, category = Category.HOLIDAY)
class Halloween : BbApiSlashCommand("halloween")
