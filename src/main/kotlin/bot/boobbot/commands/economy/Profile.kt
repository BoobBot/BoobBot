package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Formats.progressPercentage
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.math.pow


@CommandProperties(description = "View your economy profile.", category = Category.ECONOMY, groupByCategory = true)
@Option(name = "user", description = "The user whose profile you want to view. Defaults to you.", type = OptionType.USER, required = false)
class Profile : Command {

    override fun execute(ctx: Context) {
        val user = ctx.options.getByNameOrNext("user", Resolver.CONTEXT_AWARE_USER(ctx)) ?: ctx.user
        val u = BoobBot.database.getUser(user.id)
        val e = ((u.level + 1).toDouble() * 10).pow(2.0).toInt()

        ctx.reply {
            setAuthor("Profile for : ${user.asTag}", user.avatarUrl, user.avatarUrl)
            setColor(Colors.getEffectiveColor(ctx.member))
            addField(
                Formats.info("**Level**"),
                "**Current Level**: ${u.level}\n**Next Level**: ${(u.level + 1)} " +
                        "`${progressPercentage(u.experience, e)}`\n" +
                        "**Experience**: ${u.experience}/$e\n**Lewd Level**: ${u.lewdLevel}\n" +
                        "**Lewd Points**: ${u.lewdPoints}\n",
                false
            )
            addField(
                Formats.info("**Balance Information**"), "" +
                        "**Current Balance**: ${u.balance}$\n" +
                        "**Total Assets**: ${(u.balance + u.bankBalance)}$", false
            )

            addField(
                Formats.info("**General Information**"),
                "**Protected**: ${u.protected}\n" +
                        "**Jailed**: ${u.inJail}\n" +
                        "**Commands Used**:\nsfw: ${u.commandsUsed}\nnsfw: ${u.nsfwCommandsUsed}\ntotal: ${(u.commandsUsed + u.nsfwCommandsUsed)}\n" +
                        "**Messages Sent**:\nsfw: ${u.messagesSent}\nnsfw: ${u.nsfwMessagesSent}\ntotal: ${(u.messagesSent + u.nsfwMessagesSent)}\n",
                false
            )
        }
    }
}
