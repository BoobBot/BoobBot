package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.SendCommand

@CommandProperties(
        description = "Sends nudes to you or another user",
        guildOnly = true,
        aliases = ["sn"],
        category = CommandProperties.category.SEND
)
class SendNudes : SendCommand("nudes", "boobs")
