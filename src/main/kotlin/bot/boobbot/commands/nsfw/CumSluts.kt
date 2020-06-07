package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(
    description = "Sticky Love! <:stickylove:440557161538650113>",
    nsfw = true,
    category = Category.GENERAL
)
class CumSluts : BbApiCommand("cumsluts")
