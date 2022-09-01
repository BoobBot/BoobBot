package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashSendCommand

@CommandProperties(
    description = "Sends dicks to you or another user",
    guildOnly = true,
    aliases = ["sd"],
    category = Category.SEND
)
class SendDick : SlashSendCommand("dicks", "penis")
