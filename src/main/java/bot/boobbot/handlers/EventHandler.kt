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
import net.dv8tion.jda.core.entities.Game
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
            BoobBot.Scheduler.scheduleAtFixedRate(Utils.auto(autoAvatar()), 1, 2, TimeUnit.HOURS)
            self = event.jda.selfUser // set self
            // health check for status page
            embeddedServer(Netty, 8888) {
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
        readyClient.close()
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
        if (!BoobBot.isReady) { return }
        val jda = event!!.jda
        val guild = event.guild
        event.jda.asBot().shardManager.setGame(Game.playing("bbhelp || bbinvite"))
        BoobBot.log.info("New Guild Joined ${guild.name}(${guild.id})")
        val em = EmbedBuilder()
                .setColor(Color.green)
                .setAuthor(guild.name, guild.iconUrl, guild.iconUrl)
                .setTitle("Joined ${guild.name}")
                .setThumbnail(guild.iconUrl)
                .setDescription("Guild info")
                .addField(
                        Formats.info("info"),
                        "**${guild.jda.shardInfo}**\n" +
                                "Guilds: **${jda.asBot().shardManager.guilds.size}**\n" +
                                "Owner: **${guild.members.size}**\n" +
                                "Guild Users: **${guild.owner.effectiveName}**\n",
                        false)
                .setTimestamp(now())
                .build()
        val guildJoinClient = WebhookClientBuilder(Constants.GJLOG_WEBHOOK).build()
        try {
            guildJoinClient.send(
                    WebhookMessageBuilder()
                            .addEmbeds(em)
                            .setUsername(guild.name)
                            .setAvatarUrl(guild.iconUrl)
                            .build())
            guildJoinClient.close()
        } catch (ex: java.lang.Exception){
            guildJoinClient.close()
            BoobBot.log.warn("error on Guild join event", ex)
        }
    }

    override fun onGuildLeave(event: GuildLeaveEvent?) {
        if (!BoobBot.isReady) { return }
        val jda = event!!.jda
        val guild = event.guild
        event.jda.asBot().shardManager.setGame(Game.playing("bbhelp || bbinvite"))
        BoobBot.log.info("Guild left ${guild.name}(${guild.id})")
        val guildLeaveClient = WebhookClientBuilder(Constants.GLLOG_WEBHOOK).build()
        try {
        guildLeaveClient.send(
                WebhookMessageBuilder()
                        .addEmbeds(
                                EmbedBuilder()
                                        .setColor(Color.red)
                                        .setAuthor(guild.name, guild.iconUrl, guild.iconUrl)
                                        .setTitle("Left ${guild.name}")
                                        .setThumbnail(guild.iconUrl)
                                        .setDescription("Guild info")
                                        .addField(
                                                Formats.info("info"),
                                                "**${guild.jda.shardInfo}**\n" +
                                                        "Guilds: **${jda.asBot().shardManager.guilds.size}**\n" +
                                                        "Owner: **${guild.members.size}**\n" +
                                                        "Guild Users: **${guild.owner.effectiveName}**\n",
                                                false)
                                        .build())
                        .setUsername(guild.name)
                        .setAvatarUrl(guild.iconUrl)
                        .build())
        guildLeaveClient.close() } catch (ex: Exception){
            guildLeaveClient.close()
            BoobBot.log.warn("error on Guild leave event", ex)
        }
    }
}
