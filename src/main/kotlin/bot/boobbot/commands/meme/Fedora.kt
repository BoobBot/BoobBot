package bot.boobbot.commands.meme

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.MemeAvatarCommand

@CommandProperties(description = "Fedora.", category = Category.MEME, guildOnly = true, aliases = ["tip"])
class Fedora : MemeAvatarCommand("fedora")