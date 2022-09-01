package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashSendCommand

@CommandProperties(
    description = "Sends pegging to you or another user!",
    guildOnly = true,
    category = Category.SEND,
    donorOnly = true
)
class SendPegging : SlashSendCommand("pegged", "pegged")
