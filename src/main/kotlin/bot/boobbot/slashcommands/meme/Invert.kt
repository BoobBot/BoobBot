package bot.boobbot.slashcommands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.MemeAvatarSlashCommand

@CommandProperties(description = "Invert.", category = Category.MEME, guildOnly = true)
class Invert : MemeAvatarSlashCommand("invert")