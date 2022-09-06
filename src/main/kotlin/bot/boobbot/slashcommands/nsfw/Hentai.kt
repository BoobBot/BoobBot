package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Hentai.", nsfw = true, category = Category.FANTASY)
class Hentai : BbApiSlashCommand("hentai")
