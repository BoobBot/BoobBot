package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(
    description = "4K Hotness!",
    nsfw = true,
    category = Category.GENERAL,
    donorOnly = true
)
class `4K` : BbApiCommand("4k")
