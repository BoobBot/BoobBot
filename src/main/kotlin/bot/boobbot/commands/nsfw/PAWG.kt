package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(
    description = "Phat Ass White Girls!",
    nsfw = true,
    category = Category.KINKS,
    donorOnly = true
)
class PAWG : BbApiCommand("pawg")
