package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.SendCommand

@CommandProperties(
    description = "Sends pegging to you or another user!",
    guildOnly = true,
    category = Category.SEND,
    donorOnly = true
)
class SendPegging : SendCommand("pegged", "pegged")
