package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.SendCommand

@CommandProperties(
    description = "Sends thighs to you or another user.",
    guildOnly = true,
    aliases = ["st"],
    category = Category.SEND,
    donorOnly = true
)
class SendThigh : SendCommand("thighs", "ThighBot")
