package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.SendCommand

@CommandProperties(description = "Sends thighs to you or another user", guildOnly = true, aliases = ["st"])
class SendThigh : SendCommand("thighs", "ThighBot")
