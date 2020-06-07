package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(
    description = "tentacles.",
    nsfw = true,
    category = Category.KINKS,
    aliases = ["aly"]
)
class Tentacle : BbApiCommand("tentacle")
