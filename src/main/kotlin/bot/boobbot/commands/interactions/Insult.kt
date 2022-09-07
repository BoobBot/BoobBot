package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.FunCommand
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(description = "Insult someone.", category = Category.FUN, aliases = ["ins"])
@Option(name = "user", description = "The user to interact with.", type = OptionType.USER)
class Insult : FunCommand("insult")
