package bot.boobbot.commands.`fun`

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.MemeAvatarCommand

@CommandProperties(description = "Hitler.", nsfw = false, category = Category.FUN, guildOnly = true)
class Hitler : MemeAvatarCommand("hitler")