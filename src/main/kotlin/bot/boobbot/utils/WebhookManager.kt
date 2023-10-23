package bot.boobbot.utils

import bot.boobbot.BoobBot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.IncomingWebhookClient
import net.dv8tion.jda.api.entities.WebhookClient
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.awt.Color
import java.time.Instant

object WebhookManager {
    private val shardHook = createWebhookClient(BoobBot.config.RDY_WEBHOOK)
    private val leaveHook = createWebhookClient(BoobBot.config.GLLOG_WEBHOOK)
    private val joinHook = createWebhookClient(BoobBot.config.GJLOG_WEBHOOK)

    private fun createWebhookClient(webhook: String): IncomingWebhookClient {
        return WebhookClient.createClient(BoobBot.shardManager.shards.first(), webhook)
    }

    private fun safeSend(whClient: IncomingWebhookClient, avatar: String?, builder: EmbedBuilder.() -> Unit) {
        if (BoobBot.isDebug) {
            return
        }

        val message = MessageCreateData.fromEmbeds(
            EmbedBuilder()
                .setColor(Color.magenta)
                .setAuthor("BoobBot", avatar, avatar)
                .setTimestamp(Instant.now())
                .apply(builder)
                .build()
        )

        whClient.sendMessage(message)
            .setUsername(whClient.jda.selfUser.name)
            .setAvatarUrl(whClient.jda.selfUser.avatarUrl)
            .queue()
    }

    fun sendShard(avatar: String?, builder: EmbedBuilder.() -> Unit) = safeSend(shardHook, avatar, builder)
    fun sendLeave(builder: EmbedBuilder.() -> Unit) = safeSend(leaveHook, null, builder)
    fun sendJoin(builder: EmbedBuilder.() -> Unit) = safeSend(joinHook, null, builder)
}
