package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SendCommand

@CommandProperties(
    description = "Sends Tentacles to you or another user",
    guildOnly = true,
    aliases = ["sendaly"],
    category = Category.SEND
)
class SendTentacle : SendCommand("Tentacles", "tentacle")
