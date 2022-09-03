package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@CommandProperties(description = "See your current rank info.", aliases = ["level", "lvl"], category = Category.ECONOMY)
class Rank : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {
        val user = event.getOption("member")?.asUser ?: event.user
        val u = BoobBot.database.getUser(user.id)

        event.replyEmbeds(
            EmbedBuilder().apply {
            setColor(Colors.rndColor)
            addField(
                Formats.info("**Rank Information**"),
                "**Current Rep**: ${u.rep}\n" +
                        "**Current Level**: ${u.level}\n" +
                        "**Current Lewd Level**: ${(u.lewdLevel)}",
                false
            )
        }.build()).queue()
    }

}
