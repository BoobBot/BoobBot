package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SendCommand

@CommandProperties(
    description = "Sends nudes to you or another user",
    guildOnly = true,
    aliases = ["sn"],
    category = Category.SEND
)
class SendNudes : SendCommand("nudes", "boobs")
