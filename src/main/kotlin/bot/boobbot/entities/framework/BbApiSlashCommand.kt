package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncCommand
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.internal.JDAImpl
import net.dv8tion.jda.internal.entities.UserImpl
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf
import java.time.Instant

abstract class BbApiSlashCommand(private val category: String) : AsyncSlashCommand {

    private val headers = headersOf("Key", BoobBot.config.BB_API_KEY)

    override suspend fun executeAsync(event: SlashCommandInteractionEvent) {
        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return event.reply("\uD83D\uDEAB oh? something broken af").queue()

        val link = res.getString("url")
       val anonymousUser: User = UserImpl(0L, event.jda as JDAImpl)
            .setBot(false)
            .setName("Hidden User")
            .setDiscriminator("0000")
        var requester = event.user
        if (BoobBot.database.getUserAnonymity(event.user.id)) {
            requester = anonymousUser
        }
        event.replyEmbeds(
            EmbedBuilder().apply {
                setTitle("${Formats.LEWD_EMOTE} Click me!", "https://discord.boob.bot")
                setColor(Colors.rndColor)
                setImage(link)
                setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
                setTimestamp(Instant.now())
            }.build()
        ).queue()

    }
}
