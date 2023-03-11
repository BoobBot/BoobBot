package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(description = "Hentai.", nsfw = true, category = Category.FANTASY)
class Hentai : BbApiCommand("hentai")
