package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.impl.BbApiInteractionCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(description = "duh?", category = Category.INTERACTIONS, nsfw = true)
@Option(name = "with", description = "The user to interact with.", type = OptionType.USER)
class `69` : BbApiInteractionCommand("69", "<:bunny69:866454901269069844> %s & %s 69")
