package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.SlashSendCommand

@CommandProperties(
    description = "Sends some pussy to you or another user.",
    guildOnly = true,
    aliases = ["sp"],
    category = Category.SEND,
    donorOnly = true
)
class SendPussy : SlashSendCommand("pussy", "pussy")
