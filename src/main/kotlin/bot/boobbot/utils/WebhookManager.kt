package bot.boobbot.utils

import bot.boobbot.BoobBot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.IncomingWebhookClient
import net.dv8tion.jda.api.entities.WebhookClient
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.awt.Color
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object WebhookManager {
    private const val SHARD_UPDATE_KEY = "shardUpdate"
    private val statusUpdateDispatcher = Executors.newSingleThreadScheduledExecutor()

    private val shardHook = createWebhookClient(BoobBot.config.RDY_WEBHOOK)
    private val leaveHook = createWebhookClient(BoobBot.config.GLLOG_WEBHOOK)
    private val joinHook = createWebhookClient(BoobBot.config.GJLOG_WEBHOOK)

    private val batches = ConcurrentHashMap<String, StringBuilder>()
    private val lock = Object()

    init {
        statusUpdateDispatcher.scheduleAtFixedRate(::dispatchShardUpdates, 5, 5, TimeUnit.SECONDS)
    }

    private fun createWebhookClient(webhook: String): IncomingWebhookClient {
        return WebhookClient.createClient(BoobBot.shardManager.shards.first(), webhook)
    }

    private fun getBuilderFor(key: String): StringBuilder {
        return batches.computeIfAbsent(key) { StringBuilder(2000) }
    }

    fun queueShardStatusUpdate(shardId: Int, status: JDA.Status, wsPing: Long) {
        synchronized(lock) {
            val builder = getBuilderFor(SHARD_UPDATE_KEY)
            builder.appendLine("Shard **${shardId}** => `${status}`. WS: `$wsPing`ms")
        }
    }

    private fun dispatchShardUpdates() {
        synchronized(lock) {
            val builder = getBuilderFor(SHARD_UPDATE_KEY).takeIf { it.isNotEmpty() } ?: return
            val content = builder.toString()

            builder.setLength(0)

            safeSend(shardHook, null) {
                setDescription(content)
            }
        }
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
            .runCatching { queue() }
            .onFailure { it.printStackTrace() }
    }

    fun sendLeave(builder: EmbedBuilder.() -> Unit) = safeSend(leaveHook, null, builder)
    fun sendJoin(builder: EmbedBuilder.() -> Unit) = safeSend(joinHook, null, builder)
}
