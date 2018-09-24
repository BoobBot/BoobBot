package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.misc.Constants
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import bot.boobbot.misc.Utils.Companion.autoAvatar
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.DisconnectEvent
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.ReconnectedEvent
import net.dv8tion.jda.core.events.ResumedEvent
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.webhook.WebhookClientBuilder
import net.dv8tion.jda.webhook.WebhookMessageBuilder
import java.awt.Color
import java.time.Instant.now
import java.util.concurrent.TimeUnit


class EventHandler : ListenerAdapter() {
    var self: User? = null // just to hold self for discon webhooks
    override fun onReady(event: ReadyEvent) {
        BoobBot.log.info("Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.ping}ms, Status: ${event.jda.status}")
        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
        readyClient.send(WebhookMessageBuilder().addEmbeds(EmbedBuilder().setColor(Color.magenta)
                .setAuthor(
                    event.jda.selfUser.name,
                    event.jda.selfUser.effectiveAvatarUrl,
                    event.jda.selfUser.effectiveAvatarUrl
                ).setTitle("```Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.ping}ms, Status: ${event.jda.status}```", event.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR))
                .setTimestamp(now()).build()).setUsername(event.jda.selfUser.name).setAvatarUrl(event.jda.selfUser.effectiveAvatarUrl)
                .build())

        if (BoobBot.shardManager.statuses.entries.stream().filter { e -> e.value.name == "CONNECTED" }.count().toInt() == BoobBot.shardManager.shardsTotal - 1 && !BoobBot.isReady) {
            BoobBot.isReady = true
            BoobBot.Scheduler.scheduleAtFixedRate(Utils.auto(autoAvatar()), 0, 2, TimeUnit.HOURS)
            self = event.jda.selfUser // set self
            // health check for status page
            embeddedServer(Netty, 8008) {
                routing {
                    get("/health") {
                        call.respondText("{health: ok, ping: ${BoobBot.shardManager.averagePing}}", ContentType.Application.Json)
                    }
                }
            }.start(wait = false)
            BoobBot.log.info(Formats.getReadyFormat())
            readyClient.send(WebhookMessageBuilder().setContent("Ready").addEmbeds(EmbedBuilder().setColor(Color.magenta)
                    .setAuthor(
                            event.jda.selfUser.name,
                            event.jda.selfUser.effectiveAvatarUrl,
                            event.jda.selfUser.effectiveAvatarUrl)
                    .setTitle("${event.jda.selfUser.name} Fully Ready", event.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR))
                    .setThumbnail(event.jda.selfUser.effectiveAvatarUrl).addField("Ready info", "``` ${Formats.getReadyFormat()}```", false)
                    .setTimestamp(now())
                    .build()).setUsername(event.jda.selfUser.name).setAvatarUrl(event.jda.selfUser.effectiveAvatarUrl)
                    .build())
            readyClient.close()
        }
    }


    override fun onReconnect(event: ReconnectedEvent?) {
        BoobBot.log.info("Reconnected on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}")
        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
        try {
        readyClient.send(WebhookMessageBuilder().addEmbeds(EmbedBuilder().setColor(Color.green)
                .setAuthor(
                       self?.name,
                        self?.effectiveAvatarUrl,
                        self?.effectiveAvatarUrl
                ).setTitle("```Reconnected on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}```")
                .setTimestamp(now()).build()).setUsername(self?.name).setAvatarUrl(self?.effectiveAvatarUrl)
                .build())
            readyClient.close()
        } catch (ex : Exception) {
            readyClient.close()
            BoobBot.log.warn("error on reconnected event", ex)
        }
    }

    override fun onResume(event: ResumedEvent?) {
        BoobBot.log.info("Resumed on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}")
        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
        try {
            readyClient.send(WebhookMessageBuilder().addEmbeds(EmbedBuilder().setColor(Color.green)
                    .setAuthor(
                            self?.name,
                            self?.effectiveAvatarUrl,
                            self?.effectiveAvatarUrl
                    ).setTitle("```Resumed on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}```")
                    .setTimestamp(now()).build()).setUsername(self?.name).setAvatarUrl(self?.effectiveAvatarUrl)
                    .build())
            readyClient.close()
        } catch (ex : Exception) {
            readyClient.close()
            BoobBot.log.warn("error on resumed event", ex)
        }
    }

    override fun onDisconnect(event: DisconnectEvent?) {
        BoobBot.log.info("Disconnect on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}")
        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
        try {
            readyClient.send(WebhookMessageBuilder().addEmbeds(EmbedBuilder().setColor(Color.green)
                    .setAuthor(
                            self?.name,
                            self?.effectiveAvatarUrl,
                            self?.effectiveAvatarUrl
                    ).setTitle("```Disconnect on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}```")
                    .setTimestamp(now()).build()).setUsername(self?.name).setAvatarUrl(self?.effectiveAvatarUrl)
                    .build())
            readyClient.close()
        } catch (ex : Exception) {
            readyClient.close()
            BoobBot.log.warn("error on Disconnect event", ex)
        }
    }

    override fun onGuildJoin(event: GuildJoinEvent?) {
        BoobBot.log.info("Joined ${event?.guild?.name}")
    }

    override fun onGuildLeave(event: GuildLeaveEvent?) {
        BoobBot.log.info("left ${event?.guild?.name}")
    }
}
