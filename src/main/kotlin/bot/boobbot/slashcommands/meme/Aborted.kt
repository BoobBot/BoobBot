package bot.boobbot.slashcommands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.MemeAvatarSlashCommand

@CommandProperties(description = "aborted.", category = Category.MEME, guildOnly = true)
class Aborted : MemeAvatarSlashCommand("aborted")