package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Formats.progressPercentage
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import kotlin.math.pow


@CommandProperties(description = "View your economy profile.", category = Category.ECONOMY)
class Profile : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {
        val user = event.getOption("member")?.asUser ?: event.user
        val u = BoobBot.database.getUser(user.id)
        val e = ((u.level + 1).toDouble() * 10).pow(2.toDouble()).toInt()

        event.replyEmbeds(
            EmbedBuilder().apply {
            setAuthor("Profile for : ${user.asTag}", user.avatarUrl, user.avatarUrl)
            setColor(Colors.rndColor)
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
                        "**Messages Sent**:\nsfw: ${u.messagesSent}\nnsfw: ${u.nsfwMessagesSent}\ntotal: ${(u.messagesSent + u.nsfwMessagesSent)}\n",
                false
            )
        }.build()).queue()
    }
}
