package bot.boobbot.slashcommands.bot

import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.time.Instant

@CommandProperties(description = "Bot and support guild links", aliases = ["join", "oauth", "link", "links", "support"])
class Invite : SlashCommand {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.replyEmbeds(
            EmbedBuilder().apply {
            setColor(Colors.rndColor)
            setDescription(Formats.LING_MSG)
            setTimestamp(Instant.now())
        }.build()).addActionRow(Button.link("https://discord.boob.bot", "\uD83D\uDCAC")).queue()
    }
}