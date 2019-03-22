package bot.boobbot.commands.`fun`

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.MemeAvatarCommand

@CommandProperties(description = "Trigger.", category = Category.FUN, guildOnly = true, aliases = ["triggered"])
class Trigger : MemeAvatarCommand("trigger")