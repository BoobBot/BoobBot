package bot.boobbot.commands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.MemeAvatarCommand

@CommandProperties(description = "Warp.", category = Category.MEME, guildOnly = true)
class Warp : MemeAvatarCommand("warp")