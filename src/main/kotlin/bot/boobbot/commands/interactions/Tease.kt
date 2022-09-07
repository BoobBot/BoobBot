package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.impl.BbApiInteractionCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(description = "Tease someone.", category = Category.INTERACTIONS, nsfw = true)
@Option(name = "with", description = "The user to interact with.", type = OptionType.USER)
class Tease : BbApiInteractionCommand("tease", "<a:tease:866433430744596510> %s teases %s")
