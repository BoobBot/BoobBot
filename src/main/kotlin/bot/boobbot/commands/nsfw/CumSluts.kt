package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(
    description = "Sticky Love! <:stickylove:440557161538650113>",
    nsfw = true,
    category = Category.GENERAL
)
class CumSluts : BbApiCommand("cumsluts")
