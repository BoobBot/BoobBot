package bot.boobbot.slashcommands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Command
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.components.Button
import java.time.Instant

@CommandProperties(description = "Bot and support guild links", aliases = ["join", "oauth", "link", "links", "support"])
class Invite : SlashCommand {

    override fun execute(event: SlashCommandEvent) {

        event.replyEmbeds(
            EmbedBuilder().apply {
            setColor(Colors.rndColor)
            setDescription(Formats.LING_MSG)
            setTimestamp(Instant.now())
        }.build()).addActionRow(Button.link("https://discord.boob.bot", "<:discord:486939267365470210>")).queue()
    }

}