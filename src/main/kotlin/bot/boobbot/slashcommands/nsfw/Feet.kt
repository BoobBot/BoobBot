package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(
    description = "feet.",
    nsfw = true,
    category = Category.KINKS,
    aliases = ["dyna"]
)
class Feet : BbApiSlashCommand("feet")
