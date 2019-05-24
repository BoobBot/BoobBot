package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.BoobBot.Companion.config
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import de.mxro.metrics.jre.Metrics
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.events.DisconnectEvent
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.ReconnectedEvent
import net.dv8tion.jda.core.events.ResumedEvent
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.webhook.WebhookClient
import net.dv8tion.jda.webhook.WebhookClientBuilder
import net.dv8tion.jda.webhook.WebhookMessage
import net.dv8tion.jda.webhook.WebhookMessageBuilder
import java.awt.Color
import java.time.Instant
import java.util.concurrent.TimeUnit


class EventHandler : ListenerAdapter() {
    private val shardHook = WebhookClientBuilder(config.readyWebhook).build()
    private val leaveHook = WebhookClientBuilder(config.glWebhook).build()
    private val joinHook = WebhookClientBuilder(config.gjWebhook).build()

    var avatar: String? = null

    fun composeEmbed(jda: JDA, builder: EmbedBuilder.() -> Unit): WebhookMessage {
        val username = jda.selfUser?.name ?: "BoobBot"

        return WebhookMessageBuilder()
            .setUsername(username)
            .setAvatarUrl(avatar)
            .addEmbeds(
                EmbedBuilder()
                    .setColor(Color.magenta) // defaults, can be overridden with `.apply`
                    .setAuthor(
                        username,
                        avatar,
                        avatar
                    )
                    .setTimestamp(Instant.now())
                    .apply(builder)
                    .build()
            )
            .build()
    }

    fun safeSend(whClient: WebhookClient, message: WebhookMessage) {
        try {
            whClient.send(message)
        } catch (e: Exception) {
            BoobBot.log.error("Failed to send message to webhook", e)
        }
    }

    fun allShardsReady(): Boolean {
        return BoobBot.shardManager.shards.all { it.status == JDA.Status.CONNECTED || it.status == JDA.Status.LOADING_SUBSYSTEMS }
        // LOADING_SYSTEMS = receiving info from Discord, so technically connected, and should be ready in a few seconds.
    }

    override fun onReady(event: ReadyEvent) {
        BoobBot.metrics.record(Metrics.happened("Ready"))
        BoobBot.log.info("Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.ping}ms, Status: ${event.jda.status}")

        avatar = event.jda.selfUser.effectiveAvatarUrl

        safeSend(shardHook, composeEmbed(event.jda) {
            setTitle("SHARD READY [${event.jda.shardInfo.shardId}]", BoobBot.inviteUrl)
            setDescription("Ping: ${event.jda.ping}ms") // Don't need status as it's included in title
        })

        // ReadyCount is a bad way of tracking, because Shards can emit ready multiple times.
        if (allShardsReady() && !BoobBot.isReady) {
            BoobBot.isReady = true
            if (!BoobBot.isDebug) { // dont need this is testing
                BoobBot.scheduler.scheduleAtFixedRate(Utils.auto(), 4, 5, TimeUnit.HOURS)
            }
            BoobBot.shardManager.setPresence(OnlineStatus.ONLINE, Game.playing("bbhelp || bbinvite"))
            BoobBot.log.info(Formats.getReadyFormat())

            safeSend(shardHook, composeEmbed(event.jda) {
                setTitle("ALL SHARDS CONNECTED", BoobBot.inviteUrl)
                setDescription("Average Shard Ping: ${BoobBot.shardManager.averagePing}ms")
                setThumbnail(event.jda.selfUser.effectiveAvatarUrl)
                addField("Ready Info", "```\n${Formats.getReadyFormat()}```", false)
            })
        }
    }

    override fun onReconnect(event: ReconnectedEvent) {
        BoobBot.metrics.record(Metrics.happened("Reconnected"))
        BoobBot.log.info("Reconnected on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        safeSend(shardHook, composeEmbed(event.jda) {
            setTitle("SHARD RECONNECTED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.ping}ms")
        })
    }

    override fun onResume(event: ResumedEvent) {
        BoobBot.metrics.record(Metrics.happened("Resumed"))
        BoobBot.log.info("Resumed on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        safeSend(shardHook, composeEmbed(event.jda) {
            setTitle("SHARD RESUMED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.ping}ms")
        })
    }

    override fun onDisconnect(event: DisconnectEvent) {
        BoobBot.metrics.record(Metrics.happened("Disconnect"))
        BoobBot.log.info("Disconnect on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        safeSend(shardHook, composeEmbed(event.jda) {
            setTitle("SHARD DISCONNECTED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.ping}ms") // will probably be -1 or something lol
        })
    }

    override fun onGuildJoin(event: GuildJoinEvent) {
        BoobBot.metrics.record(Metrics.happened("GuildJoin"))
        // Don't set presence on guildJoin and leave, this is probably an easy way to hit ratelimit if someone spams.

        val guild = event.guild

        safeSend(joinHook, composeEmbed(event.jda) {
            setColor(Color.green)
            setTitle("Guild Joined: ${guild.name}")
            setDescription(
                "${Formats.info("info")}\n" +
                        "On Shard: ${event.jda.shardInfo.shardId}\n" +
                        "Total Guilds: ${BoobBot.shardManager.guilds.size}\n\n" +
                        "Owner: ${guild.owner.user.asTag} (${guild.owner.user.id})\n" +
                        "Members: ${guild.members.size}"
            )
            setThumbnail(guild.iconUrl)
        })
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        BoobBot.metrics.record(Metrics.happened("GuildLeave"))
        val guild = event.guild

        safeSend(leaveHook, composeEmbed(event.jda) {
            setColor(Color.red)
            setTitle("Guild Left: ${guild.name}")
            setDescription(
                "${Formats.info("info")}\n" +
                        "On Shard: ${event.jda.shardInfo.shardId}\n" +
                        "Total Guilds: ${BoobBot.shardManager.guilds.size}\n\n" +
                        "Owner: ${guild.owner.user.asTag} (${guild.owner.user.id})\n" +
                        "Members: ${guild.members.size}"
            )
            setThumbnail(guild.iconUrl)
        })
    }
}
