package bot.boobbot.commands.`fun`

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.MemeAvatarCommand

@CommandProperties(description = "Trigger.", category = Category.FUN, guildOnly = true, aliases = ["triggered"])
class Trigger : MemeAvatarCommand("trigger")