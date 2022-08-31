package bot.boobbot.slashcommands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.MemeAvatarSlashCommand

@CommandProperties(description = "Warp.", category = Category.MEME, guildOnly = true)
class Warp : MemeAvatarSlashCommand("warp")