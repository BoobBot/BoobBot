package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiSlashCommand

@CommandProperties(
    description = "Phat Ass White Girls!",
    nsfw = true,
    category = Category.KINKS,
    donorOnly = true
)
class PAWG : BbApiSlashCommand("pawg")
