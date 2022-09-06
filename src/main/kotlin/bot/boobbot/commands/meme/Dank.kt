package bot.boobbot.commands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.MemeAvatarCommand

@CommandProperties(description = "Dank.", category = Category.MEME, guildOnly = true)
class Dank : MemeAvatarCommand("dank")