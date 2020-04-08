package bot.boobbot.commands.meme

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.MemeAvatarCommand

@CommandProperties(description = "goggles.", category = Category.MEME, guildOnly = true)
class Goggles : MemeAvatarCommand("goggles")