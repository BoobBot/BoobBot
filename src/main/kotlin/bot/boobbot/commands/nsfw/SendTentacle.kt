package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.SendCommand

@CommandProperties(
    description = "Sends Tentacles to you or another user",
    guildOnly = true,
    aliases = ["sendaly"],
    category = Category.SEND
)
class SendTentacle : SendCommand("Tentacles", "tentacle")
