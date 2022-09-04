package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(
    description = "feet.",
    nsfw = true,
    category = Category.KINKS,
    aliases = ["dyna"]
)
class Feet : BbApiCommand("feet")
