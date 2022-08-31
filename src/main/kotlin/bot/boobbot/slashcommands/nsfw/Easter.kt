package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiSlashCommand

@CommandProperties(description = "Easter is nice \uD83D\uDC30", nsfw = true, category = Category.HOLIDAY)
class Easter : BbApiSlashCommand("easter")
