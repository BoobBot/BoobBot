package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Formats.progressPercentage
import kotlin.math.pow


@CommandProperties(description = "get a Profile", category = Category.ECONOMY, developerOnly = true)
class Profile : Command {

    override fun execute(ctx: Context) {
        val user = ctx.mentions.firstOrNull() ?: ctx.author
        val u = BoobBot.database.getUser(user.id)
        val e = ((u.level + 1).toDouble() * 10).pow(2.toDouble()).toInt()
        val s = StringBuilder()
        val em = ctx.embed {
            setAuthor("Profile for : ${user.asTag}", user.avatarUrl, user.avatarUrl)
            setColor(Colors.getEffectiveColor(ctx.message))
            addField(
                Formats.info("**Level**"),
                "**Current Level**: ${u.level}\n**Next Level**: ${(u.level + 1)} " +
                        "`${progressPercentage(
                            u.experience,
                            e
                        )}`\n" +
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
                        "**Messages Seen**:\nsfw: ${u.messagesSent}\nnsfw: ${u.nsfwMessagesSent}\ntotal: ${(u.messagesSent + u.nsfwMessagesSent)}\n",
                false
            )


        }

    }
}
