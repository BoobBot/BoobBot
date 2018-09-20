package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.SendCommand

@CommandProperties(
        description = "Sends thighs to you or another user. <:p_:475801484282429450>",
        guildOnly = true,
        aliases = ["st"],
        category = CommandProperties.category.SEND,
        donorOnly = true
)
class SendThigh : SendCommand("thighs", "ThighBot")
