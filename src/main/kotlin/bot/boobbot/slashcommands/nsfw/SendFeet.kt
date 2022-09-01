package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashSendCommand


@CommandProperties(
    description = "Sends feet to you or another user",
    guildOnly = true,
    aliases = ["senddyna"],
    category = Category.SEND
)
class SendFeet : SlashSendCommand("feet", "feet")
