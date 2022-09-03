package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@CommandProperties(description = "See your current balance.", aliases = ["bal", "$"], category = Category.ECONOMY)
class Balance : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {
        val user = event.getOption("member")?.asUser ?: event.user
        val u = BoobBot.database.getUser(user.id)

        event.replyEmbeds(
            EmbedBuilder().apply {
                setColor(Colors.rndColor)
            addField(
                Formats.info("**Balance Information**"),
                "**Current Balance**: $${u.balance}\n" +
                        "**Bank Balance**: $${u.bankBalance}\n" +
                        "**Total Assets**: $${(u.balance + u.bankBalance)}",
                false
            )
        }.build()).queue()
    }

}
