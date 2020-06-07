package bot.boobbot.misc

import bot.boobbot.BoobBot
import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.exception.HttpException
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.time.Instant

object WebhookManager {
    private val shardHook = WebhookClientBuilder(BoobBot.config.RDY_WEBHOOK).build()
    private val leaveHook = WebhookClientBuilder(BoobBot.config.GLLOG_WEBHOOK).build()
    private val joinHook = WebhookClientBuilder(BoobBot.config.GJLOG_WEBHOOK).build()

    private fun safeSend(whClient: WebhookClient, avatar: String?, builder: EmbedBuilder.() -> Unit) {
        val message = WebhookMessageBuilder()
            .setUsername("BoobBot")
            .setAvatarUrl(avatar)
            .addEmbeds(
                EmbedBuilder()
                    .apply {
                        setColor(Color.magenta)
                        setAuthor("BoobBot", avatar, avatar)
                        setTimestamp(Instant.now())
                    }
                    .apply(builder)
                    .build()
                    .toWebhookEmbed()
            )
            .build()

        try {
            if (!BoobBot.isDebug) {
                whClient.send(message)
            }
        } catch (e: HttpException) {
            if (!e.localizedMessage.contains("Unknown Webhook")) {
                BoobBot.log.error("Failed to send message to webhook", e)
            }
        }
    }

    fun sendShard(avatar: String?, builder: EmbedBuilder.() -> Unit) = safeSend(shardHook, avatar, builder)
    fun sendLeave(builder: EmbedBuilder.() -> Unit) = safeSend(leaveHook, null, builder)
    fun sendJoin(builder: EmbedBuilder.() -> Unit) = safeSend(joinHook, null, builder)
}
