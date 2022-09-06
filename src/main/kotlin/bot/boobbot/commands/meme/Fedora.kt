package bot.boobbot.commands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.MemeAvatarCommand

@CommandProperties(description = "Fedora.", category = Category.MEME, guildOnly = true, aliases = ["tip"])
class Fedora : MemeAvatarCommand("fedora")