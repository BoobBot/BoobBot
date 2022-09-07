package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.interactions.commands.OptionType


@CommandProperties(description = "See your current rank info.", aliases = ["level", "lvl"], category = Category.ECONOMY)
@Option(name = "user", description = "Who do you want to view the rank of? Defaults to you.", type = OptionType.USER, required = false)
class Rank : Command {

    override fun execute(ctx: Context) {
        val user = ctx.options.getByNameOrNext("user", Resolver.USER) ?: ctx.user
        val u = BoobBot.database.getUser(user.id)

        ctx.reply {
            setColor(Colors.getEffectiveColor(ctx.member))
            addField(
                Formats.info("**Rank Information**"),
                "**Current Rep**: ${u.rep}\n" +
                        "**Current Level**: ${u.level}\n" +
                        "**Current Lewd Level**: ${(u.lewdLevel)}",
                false
            )
        }
    }

}
