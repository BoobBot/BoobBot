package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(
    description = "Redheads: because redder is better! <:p_:475801484282429450> ",
    aliases = ["redhead", "redheads"],
    nsfw = true,
    category = Category.GENERAL,
    donorOnly = true
)
class Red : BbApiCommand("red")
