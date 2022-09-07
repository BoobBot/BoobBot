package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.SendCommand
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(
    description = "Sends Tentacles to you or another user",
    guildOnly = true,
    aliases = ["sendaly"],
    category = Category.SEND
)
@Option(name = "to", description = "The user to send to.", type = OptionType.USER, required = false)
class SendTentacle : SendCommand("tentacles", "tentacle")
