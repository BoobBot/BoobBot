package bot.boobbot.slashcommands.`fun`

import bot.boobbot.entities.framework.*

@CommandProperties(description = "random cat", category = Category.FUN, aliases = ["cat"])
class Meow : NekoLifeApiSlashCommand("meow")