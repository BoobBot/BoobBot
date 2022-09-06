package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(description = "Tiny girls!", nsfw = true, category = Category.KINKS)
class Tiny : BbApiSlashCommand("tiny")
