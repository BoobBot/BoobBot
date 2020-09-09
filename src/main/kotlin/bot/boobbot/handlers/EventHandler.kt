package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.utils.Formats
import bot.boobbot.utils.WebhookManager
import de.mxro.metrics.jre.Metrics
import net.dv8tion.jda.api.events.DisconnectEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.ReconnectedEvent
import net.dv8tion.jda.api.events.ResumedEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color


class EventHandler : ListenerAdapter() {
    private var avatar: String? = "https://boob.bot/android-chrome-192x192.png"
    var readyCount = 0
    var shardWebhookMessage = ""
    override fun onReady(event: ReadyEvent) {
        readyCount++
        shardWebhookMessage += "Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.gatewayPing}ms, Status: ${event.jda.status}\n"
        print(shardWebhookMessage)
        BoobBot.metrics.record(Metrics.happened("Ready"))
        BoobBot.log.info("Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.gatewayPing}ms, Status: ${event.jda.status}")
        if (readyCount == 16) {
            readyCount = 0
            WebhookManager.sendShard(avatar) {
                setTitle("Ready info", BoobBot.inviteUrl)
                setDescription(shardWebhookMessage)
            }
            shardWebhookMessage = ""
        }
    }

    override fun onReconnect(event: ReconnectedEvent) {
        BoobBot.metrics.record(Metrics.happened("Reconnected"))
        BoobBot.log.info("Reconnected on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        WebhookManager.sendShard(avatar) {
            setTitle("SHARD RECONNECTED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms")
        }
    }

    override fun onResume(event: ResumedEvent) {
        BoobBot.metrics.record(Metrics.happened("Resumed"))
        BoobBot.log.info("Resumed on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        WebhookManager.sendShard(avatar) {
            setTitle("SHARD RESUMED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms")
        }
    }

    override fun onDisconnect(event: DisconnectEvent) {
        BoobBot.metrics.record(Metrics.happened("Disconnect"))
        BoobBot.log.info("Disconnect on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        WebhookManager.sendShard(avatar) {
            setTitle("SHARD DISCONNECTED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms") // will probably be -1 or something lol
        }
    }

    override fun onGuildJoin(event: GuildJoinEvent) {
        BoobBot.metrics.record(Metrics.happened("GuildJoin"))
        val guild = event.guild

        WebhookManager.sendJoin {
            setColor(Color.green)
            setTitle("Guild Joined: ${guild.name}")
            setDescription(
                "${Formats.info("info")}\n" +
                        "On Shard: ${event.jda.shardInfo.shardId}\n" +
                        "Total Guilds: ${BoobBot.shardManager.guilds.size}\n\n" +
                        "OwnerID: ${guild.ownerId})\n" +
                        "Members: ${guild.memberCount}"
            )
            setThumbnail(guild.iconUrl)
        }
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        BoobBot.metrics.record(Metrics.happened("GuildLeave"))
        val guild = event.guild

        WebhookManager.sendLeave {
            setColor(Color.red)
            setTitle("Guild Left: ${guild.name}")
            setDescription(
                "${Formats.info("info")}\n" +
                        "On Shard: ${event.jda.shardInfo.shardId}\n" +
                        "Total Guilds: ${BoobBot.shardManager.guilds.size}\n\n" +
                        "OwnerID: ${guild.ownerId})\n" +
                        "Members: ${guild.memberCount}"
            )
            setThumbnail(guild.iconUrl)
        }
    }
}
