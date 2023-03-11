package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.utils.Formats
import bot.boobbot.utils.WebhookManager
import de.mxro.metrics.jre.Metrics
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.http.HttpRequestEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent
import net.dv8tion.jda.api.events.session.SessionRecreateEvent
import net.dv8tion.jda.api.events.session.SessionResumeEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.awt.Color


class EventHandler : EventListener {
    private var avatar: String? = "https://boob.bot/android-chrome-192x192.png"
    var readyCount = 0
    var shardWebhookMessage = ""

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is ReadyEvent -> onReady(event)
            is SessionRecreateEvent -> onReconnect(event)
            is SessionResumeEvent -> onResume(event)
            is SessionDisconnectEvent -> onDisconnect(event)
            is HttpRequestEvent -> onHttpRequest()
            is GuildJoinEvent -> onGuildJoin(event)
            is GuildLeaveEvent -> onGuildLeave(event)
        }
    }

    private fun onReady(event: ReadyEvent) {
        readyCount++
        shardWebhookMessage += "`Ready on shard: ${event.jda.shardInfo.shardId} Ping: ${event.jda.gatewayPing}ms Status: ${event.jda.status}`\n"
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

    private fun onReconnect(event: SessionRecreateEvent) {
        BoobBot.metrics.record(Metrics.happened("Reconnected"))
        BoobBot.log.info("Reconnected on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        WebhookManager.sendShard(avatar) {
            setTitle("SHARD RECONNECTED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms")
        }
    }

    private fun onResume(event: SessionResumeEvent) {
        BoobBot.metrics.record(Metrics.happened("Resumed"))
        BoobBot.log.info("Resumed on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        WebhookManager.sendShard(avatar) {
            setTitle("SHARD RESUMED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms")
        }
    }

    private fun onDisconnect(event: SessionDisconnectEvent) {
        BoobBot.metrics.record(Metrics.happened("Disconnect"))
        BoobBot.log.info("Disconnect on shard: ${event.jda.shardInfo.shardId}, Status: ${event.jda.status}")

        WebhookManager.sendShard(avatar) {
            setTitle("SHARD DISCONNECTED [${event.jda.shardInfo.shardId}]")
            setDescription("Ping: ${event.jda.gatewayPing}ms") // will probably be -1 or something lol
        }
    }

    private fun onHttpRequest() = BoobBot.metrics.record(Metrics.happened("HttpRequest"))

    private fun onGuildJoin(event: GuildJoinEvent) {
        BoobBot.metrics.record(Metrics.happened("GuildJoin"))

        WebhookManager.sendJoin {
            buildGuildEmbed(this, event.guild, true)
        }
    }

    private fun onGuildLeave(event: GuildLeaveEvent) {
        BoobBot.metrics.record(Metrics.happened("GuildLeave"))

        WebhookManager.sendLeave {
            buildGuildEmbed(this, event.guild, false)
        }
    }

    private fun buildGuildEmbed(builder: EmbedBuilder, guild: Guild, joined: Boolean) = builder.apply {
        setColor(if (joined) Color.green else Color.red)
        setTitle("Guild ${if (joined) "Joined" else "Left"}: ${guild.name}")
        setDescription(
            "${Formats.info("info")}\n" +
                    "On Shard: ${guild.jda.shardInfo.shardId}\n" +
                    "Total Guilds: ${BoobBot.shardManager.guilds.size}\n\n" +
                    "OwnerID: ${guild.ownerId})\n" +
                    "Members: ${guild.memberCount}"
        )
        setThumbnail(guild.iconUrl)
    }
}
