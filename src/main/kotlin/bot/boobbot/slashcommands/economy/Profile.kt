package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Formats.progressPercentage
import kotlin.math.pow

@CommandProperties(description = "View your economy profile.", category = Category.ECONOMY)
class Profile : SlashCommand {

    override fun execute(ctx: SlashContext) {
        val user = ctx.getOption("member")?.asUser ?: ctx.user
        val u = BoobBot.database.getUser(user.id)
        val e = ((u.level + 1).toDouble() * 10).pow(2.toDouble()).toInt()

        ctx.reply {
            setAuthor("Profile for : ${user.asTag}", user.avatarUrl, user.avatarUrl)
            setColor(Colors.rndColor)
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
