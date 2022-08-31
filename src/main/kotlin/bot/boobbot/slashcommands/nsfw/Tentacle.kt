package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiSlashCommand

@CommandProperties(
    description = "tentacles.",
    nsfw = true,
    category = Category.KINKS,
    aliases = ["aly"]
)
class Tentacle : BbApiSlashCommand("tentacle")
