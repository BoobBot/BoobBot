package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.misc.Constants
import bot.boobbot.misc.Formats
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
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


class EventHandler : ListenerAdapter() {

    override fun onReady(event: ReadyEvent) {
        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()

        readyClient.send(WebhookMessageBuilder().addEmbeds(EmbedBuilder().setColor(Color.magenta)
                .setAuthor(
                    event.jda.selfUser.name,
                    event.jda.selfUser.effectiveAvatarUrl,
                    event.jda.selfUser.effectiveAvatarUrl
                ).setTitle("```Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.ping}ms, Status: ${event.jda.status}```", event.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR))
                .setTimestamp(now()).build()).setUsername(event.jda.selfUser.name).setAvatarUrl(event.jda.selfUser.effectiveAvatarUrl)
                .build())

        BoobBot.log.info("Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.ping}ms, Status: ${event.jda.status}")

        if (BoobBot.shardManager.statuses.entries.stream().filter { e -> e.value.name == "CONNECTED" }.count().toInt() == BoobBot.shardManager.shardsTotal - 1 && !BoobBot.isReady) {
            BoobBot.isReady = true
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
        super.onReconnect(event)
    }

    override fun onResume(event: ResumedEvent?) {
        super.onResume(event)
    }

    override fun onDisconnect(event: DisconnectEvent?) {
        super.onDisconnect(event)
    }

    override fun onGuildJoin(event: GuildJoinEvent?) {
        BoobBot.log.info("Joined ${event?.guild?.name}")
    }

    override fun onGuildLeave(event: GuildLeaveEvent?) {
        BoobBot.log.info("left ${event?.guild?.name}")
    }
}
