package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(
    description = "Phat Ass White Girls!",
    nsfw = true,
    category = Category.KINKS,
    donorOnly = true
)
class PAWG : BbApiCommand("pawg")
