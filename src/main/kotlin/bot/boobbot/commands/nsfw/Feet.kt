package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(
    description = "feet.",
    nsfw = true,
    category = Category.KINKS,
    aliases = ["dyna"]
)
class Feet : BbApiCommand("feet")
