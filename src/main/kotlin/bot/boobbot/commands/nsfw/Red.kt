package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(
    description = "Redheads: because redder is better!",
    aliases = ["redhead", "redheads"],
    category = Category.GENERAL,
    donorOnly = true,
    nsfw = true
)
class Red : BbApiCommand("red")
