package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SendCommand

@CommandProperties(
    description = "Sends some pussy to you or another user. <:p_:475801484282429450>",
    guildOnly = true,
    aliases = ["sp"],
    category = Category.SEND,
    donorOnly = true
)
class SendPussy : SendCommand("pussy", "pussy")
