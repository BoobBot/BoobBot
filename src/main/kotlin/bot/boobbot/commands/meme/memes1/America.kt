package bot.boobbot.commands.meme.memes1

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.MemeAvatarCommand
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(description = "America.", category = Category.MEME, guildOnly = true, groupByCategory = true)
@Option(name = "user", description = "The user to make a meme out of.", type = OptionType.USER, required = false)
class America : MemeAvatarCommand("america")