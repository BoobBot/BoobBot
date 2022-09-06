package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.SendCommand

@CommandProperties(
    description = "Sends dicks to you or another user",
    guildOnly = true,
    aliases = ["sd"],
    category = Category.SEND
)
class SendDick : SendCommand("dicks", "penis")
