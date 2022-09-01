package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashSendCommand

@CommandProperties(
    description = "Sends nudes to you or another user",
    guildOnly = true,
    aliases = ["sn"],
    category = Category.SEND
)
class SendNudes : SlashSendCommand("nudes", "boobs")
