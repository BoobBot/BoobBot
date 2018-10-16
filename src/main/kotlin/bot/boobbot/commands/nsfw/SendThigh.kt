package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.SendCommand

@CommandProperties(
        description = "Sends thighs to you or another user. <:p_:475801484282429450>",
        guildOnly = true,
        aliases = ["st"],
        category = Category.SEND,
        donorOnly = true
)
class SendThigh : SendCommand("thighs", "ThighBot")
