package bot.boobbot.slashcommands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.MemeAvatarSlashCommand

@CommandProperties(description = "Jail.", category = Category.MEME, guildOnly = true)
class Jail : MemeAvatarSlashCommand("jail")