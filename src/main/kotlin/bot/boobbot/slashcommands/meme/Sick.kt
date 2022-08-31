package bot.boobbot.slashcommands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.MemeAvatarSlashCommand

@CommandProperties(description = "sick.", category = Category.MEME, guildOnly = true)
class Sick : MemeAvatarSlashCommand("sickban")