package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.impl.BbApiCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(
    description = "Sticky Love! <:stickylove:440557161538650113>",
    nsfw = true,
    category = Category.GENERAL
)
class CumSluts : BbApiCommand("cumsluts")
