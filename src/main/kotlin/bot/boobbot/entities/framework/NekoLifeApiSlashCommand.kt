package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import java.time.Instant

abstract class NekoLifeApiSlashCommand(private val category: String) : AsyncSlashCommand {
    override suspend fun executeAsync(event: SlashCommandEvent) {
        val res = BoobBot.requestUtil.get("https://nekos.life/api/v2/img/$category").await()?.json()
            ?: return event.reply(
                Formats.error(" oh? something broken af")
            ).queue()
        event.replyEmbeds(
            EmbedBuilder().apply {
                setTitle("Nya~", "https://nekos.life")
                setColor(Colors.rndColor)
                setImage(res.getString("url"))
                setFooter("Powered by https://nekos.life", "https://nekos.life/static/icons/favicon-194x194.png")
                setTimestamp(Instant.now())
            }.build()
        ).queue()

    }
}
