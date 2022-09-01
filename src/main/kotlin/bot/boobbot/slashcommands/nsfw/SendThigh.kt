package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashSendCommand

@CommandProperties(
    description = "Sends thighs to you or another user.",
    guildOnly = true,
    aliases = ["st"],
    category = Category.SEND,
    donorOnly = true
)
class SendThigh : SlashSendCommand("thighs", "ThighBot")
