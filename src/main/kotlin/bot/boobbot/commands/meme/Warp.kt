package bot.boobbot.commands.meme

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.MemeAvatarCommand
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(description = "Warp.", category = Category.MEME, guildOnly = true)
@Option(name = "user", description = "The user to make a meme out of.", type = OptionType.USER, required = false)
class Warp : MemeAvatarCommand("warp")