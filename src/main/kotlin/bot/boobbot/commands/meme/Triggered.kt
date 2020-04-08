package bot.boobbot.commands.meme

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.MemeAvatarCommand

@CommandProperties(description = "trigger.", category = Category.MEME, guildOnly = true)
class Triggered : MemeAvatarCommand("trigger")