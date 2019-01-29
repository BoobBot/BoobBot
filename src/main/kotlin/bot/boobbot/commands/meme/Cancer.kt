package bot.boobbot.commands.meme

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.MemeAvatarCommand

@CommandProperties(description = "Cancer.", category = Category.MEME, guildOnly = true)
class Cancer : MemeAvatarCommand("cancer")