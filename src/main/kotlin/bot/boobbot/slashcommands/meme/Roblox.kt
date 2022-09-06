package bot.boobbot.slashcommands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MemeAvatarSlashCommand

@CommandProperties(description = "Roblox.", category = Category.MEME, guildOnly = true)
class Roblox : MemeAvatarSlashCommand("roblox")