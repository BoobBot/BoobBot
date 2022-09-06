package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Hentai Traps.", nsfw = true, category = Category.FANTASY)
class Futa : BbApiSlashCommand("futa")
