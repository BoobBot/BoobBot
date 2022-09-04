package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(description = "Random thighs", nsfw = true, category = Category.KINKS)
class Thigh : BbApiSlashCommand("ThighBot")
