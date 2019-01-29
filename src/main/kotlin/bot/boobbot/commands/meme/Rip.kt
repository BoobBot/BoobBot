package bot.boobbot.commands.meme

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.MemeAvatarCommand

@CommandProperties(description = "Rip.", category = Category.MEME, guildOnly = true)
class Rip : MemeAvatarCommand("rip")