package bot.boobbot.commands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.MemeAvatarCommand

@CommandProperties(description = "Fedora.", category = Category.MEME, guildOnly = true, aliases = ["tip"])
class Fedora : MemeAvatarCommand("fedora")