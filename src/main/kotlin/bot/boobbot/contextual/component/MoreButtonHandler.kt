package bot.boobbot.contextual.component

import bot.boobbot.BoobBot
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import okhttp3.Headers
import java.time.Instant

class MoreButtonHandler : BaseButtonHandler("more:") {
    private val headers = Headers.headersOf("Key", BoobBot.config.BB_API_KEY)

    override suspend fun onButtonInteraction(event: ButtonInteractionEvent) {
        val category = event.componentId.split(':').last()

        event.deferReply().submit().await()

        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return event.reply("\uD83D\uDEAB oh? something broken af").queue()

        val link = res.getString("url")
        val requester = BoobBot.shardManager.authorOrAnonymous(event.user)

        event.hook.editOriginal(MessageEditBuilder()
            .setEmbeds(
                EmbedBuilder().apply {
                    setTitle("${Formats.LEWD_EMOTE} Click me!", "https://discord.boob.bot")
                    setColor(Colors.getEffectiveColor(event.member))
                    setImage(link)
                    setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
                    setTimestamp(Instant.now())
                }.build()
            )
            .setComponents(ActionRow.of(Button.primary("more:$category", "🔄 Next")))
            .build()
        ).queue()
    }
}
