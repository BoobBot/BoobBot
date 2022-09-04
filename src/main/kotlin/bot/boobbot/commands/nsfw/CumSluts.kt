package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties

@CommandProperties(
    description = "Sticky Love! <:stickylove:440557161538650113>",
    nsfw = true,
    category = Category.GENERAL
)
class CumSluts : BbApiCommand("cumsluts")
