package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(
    description = "Phat Ass White Girls! <:p_:475801484282429450>",
    nsfw = true,
    category = Category.KINKS,
    donorOnly = true
)
class PAWG : BbApiCommand("pawg")
