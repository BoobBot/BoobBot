package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SendCommand

@CommandProperties(
    description = "Sends pegging to you or another user! <:p_:475801484282429450>",
    guildOnly = true,
    category = Category.SEND,
    donorOnly = true
)
class SendPegging : SendCommand("pegged", "pegged")
