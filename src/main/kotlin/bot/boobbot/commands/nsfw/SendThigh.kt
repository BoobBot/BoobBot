package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.SendCommand
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(
    description = "Sends thighs to you or another user.",
    guildOnly = true,
    aliases = ["st"],
    category = Category.SEND,
    donorOnly = true,
    nsfw = true
)
@Option(name = "to", description = "The user to send to.", type = OptionType.USER, required = false)
class SendThigh : SendCommand("thighs", "ThighBot")
