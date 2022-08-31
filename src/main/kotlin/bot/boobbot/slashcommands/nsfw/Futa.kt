package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiSlashCommand

@CommandProperties(description = "Hentai Traps.", nsfw = true, category = Category.FANTASY)
class Futa : BbApiSlashCommand("futa")
