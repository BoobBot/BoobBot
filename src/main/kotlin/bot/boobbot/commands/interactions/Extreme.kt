package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.BbApiInteractionCommand
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(description = "Are you extreme?", category = Category.INTERACTIONS, nsfw = true)
@Option(name = "with", description = "The user to interact with.", type = OptionType.USER)
class Extreme : BbApiInteractionCommand("extreme", "<:extreme:866451714471231510> %s likes it extreme. Are you in %s?")
