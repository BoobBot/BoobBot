package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.BoobBot.Companion.setGame
import bot.boobbot.misc.AutoPorn
import bot.boobbot.misc.Constants
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import de.mxro.metrics.jre.Metrics
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
import org.apache.commons.lang3.StringUtils
import java.awt.Color
import java.time.Instant.now
import java.util.concurrent.TimeUnit


class EventHandler : ListenerAdapter() {
    var self: User? = null // just to hold self for discon webhooks
    override fun onReady(event: ReadyEvent) {
        BoobBot.metrics.record(Metrics.happened("Ready"))
        BoobBot.log.info("Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.ping}ms, Status: ${event.jda.status}")
        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
        readyClient.send(
            WebhookMessageBuilder().addEmbeds(
                EmbedBuilder().setColor(Color.magenta)
                    .setAuthor(
                        event.jda.selfUser.name,
                        event.jda.selfUser.effectiveAvatarUrl,
                        event.jda.selfUser.effectiveAvatarUrl
                    ).setTitle(
                        "```Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.ping}ms, Status: ${event.jda.status}```",
                        event.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR)
                    )
                    .setTimestamp(now()).build()
            ).setUsername(event.jda.selfUser.name).setAvatarUrl(event.jda.selfUser.effectiveAvatarUrl)
                .build()
        )
        if (BoobBot.shardManager.statuses.entries.stream().filter { e -> e.value.name == "CONNECTED" }.count().toInt() == BoobBot.shardManager.shardsTotal - 1 && !BoobBot.isReady) {
            BoobBot.isReady = true
            if (!BoobBot.isDebug) { // dont need this is testing
                BoobBot.Scheduler.scheduleAtFixedRate(Utils.auto(), 1, 2, TimeUnit.HOURS)
            }
            BoobBot.Scheduler.scheduleAtFixedRate(AutoPorn.auto(), 60, 45, TimeUnit.SECONDS)
            self = event.jda.selfUser // set
            BoobBot.log.info(Formats.getReadyFormat())
            readyClient.send(
                WebhookMessageBuilder().setContent("Ready").addEmbeds(
                    EmbedBuilder().setColor(Color.magenta)
                        .setAuthor(
                            event.jda.selfUser.name,
                            event.jda.selfUser.effectiveAvatarUrl,
                            event.jda.selfUser.effectiveAvatarUrl
                        )
                        .setTitle(
                            "${event.jda.selfUser.name} Fully Ready",
                            event.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR)
                        )
                        .setThumbnail(event.jda.selfUser.effectiveAvatarUrl).addField(
                            "Ready info",
                            "``` ${Formats.getReadyFormat()}```",
                            false
                        )
                        .setTimestamp(now())
                        .build()
                ).setUsername(event.jda.selfUser.name).setAvatarUrl(event.jda.selfUser.effectiveAvatarUrl)
                    .build()
            )
            readyClient.close()
        }
        readyClient.close()
    }


    override fun onReconnect(event: ReconnectedEvent?) {
        BoobBot.metrics.record(Metrics.happened("Reconnected"))
        BoobBot.log.info("Reconnected on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}")
        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
        try {
            readyClient.send(
                WebhookMessageBuilder().addEmbeds(
                    EmbedBuilder().setColor(Color.green)
                        .setAuthor(
                            self?.name,
                            self?.effectiveAvatarUrl,
                            self?.effectiveAvatarUrl
                        ).setTitle("```Reconnected on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}```")
                        .setTimestamp(now()).build()
                ).setUsername(self?.name).setAvatarUrl(self?.effectiveAvatarUrl)
                    .build()
            )
            readyClient.close()
        } catch (ex: Exception) {
            readyClient.close()
            BoobBot.log.warn("error on reconnected event", ex)
        }
    }

    override fun onResume(event: ResumedEvent?) {
        BoobBot.metrics.record(Metrics.happened("Resumed"))
        BoobBot.log.info("Resumed on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}")
        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
        try {
            readyClient.send(
                WebhookMessageBuilder().addEmbeds(
                    EmbedBuilder().setColor(Color.green)
                        .setAuthor(
                            self?.name,
                            self?.effectiveAvatarUrl,
                            self?.effectiveAvatarUrl
                        ).setTitle("```Resumed on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}```")
                        .setTimestamp(now()).build()
                ).setUsername(self?.name).setAvatarUrl(self?.effectiveAvatarUrl)
                    .build()
            )
            readyClient.close()
        } catch (ex: Exception) {
            readyClient.close()
            BoobBot.log.warn("error on resumed event", ex)
        }
    }

    override fun onDisconnect(event: DisconnectEvent?) {
        BoobBot.metrics.record(Metrics.happened("Disconnect"))
        BoobBot.log.info("Disconnect on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}")
        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
        try {
            readyClient.send(
                WebhookMessageBuilder().addEmbeds(
                    EmbedBuilder().setColor(Color.green)
                        .setAuthor(
                            self?.name,
                            self?.effectiveAvatarUrl,
                            self?.effectiveAvatarUrl
                        ).setTitle("```Disconnect on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}```")
                        .setTimestamp(now()).build()
                ).setUsername(self?.name).setAvatarUrl(self?.effectiveAvatarUrl)
                    .build()
            )
            readyClient.close()
        } catch (ex: Exception) {
            readyClient.close()
            BoobBot.log.warn("error on Disconnect event", ex)
        }
    }

    override fun onGuildJoin(event: GuildJoinEvent?) {
        BoobBot.metrics.record(Metrics.happened("GuildJoin"))
        if (!BoobBot.isReady) {
            return
        }
        val jda = event!!.jda
        val guild = event.guild
        if (!setGame) {
            event.jda.asBot().shardManager.setGame(Game.playing("bbhelp || bbinvite"))
        }
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
                        "Owner: **${guild.owner.effectiveName}**\n" +
                        "Guild Users: **${guild.members.size}**\n",
                false
            )
            .setTimestamp(now())
            .build()
        val guildJoinClient = WebhookClientBuilder(Constants.GJLOG_WEBHOOK).build()
        try {
            guildJoinClient.send(
                WebhookMessageBuilder()
                    .addEmbeds(em)
                    .setUsername(if (guild.name.length > 3) StringUtils.abbreviate(guild.name, 20) else "Shity name")
                    .setAvatarUrl(guild.iconUrl)
                    .build()
            )
            guildJoinClient.close()
        } catch (ex: java.lang.Exception) {
            guildJoinClient.close()
            BoobBot.log.warn("error on Guild join event", ex)
        }
    }

    override fun onGuildLeave(event: GuildLeaveEvent?) {
        BoobBot.metrics.record(Metrics.happened("GuildLeave"))
        if (!BoobBot.isReady) {
            return
        }
        val jda = event!!.jda
        val guild = event.guild
        if (!setGame) {
            event.jda.asBot().shardManager.setGame(Game.playing("bbhelp || bbinvite"))
        }
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
                                        "Owner: **${guild.owner.effectiveName}**\n" +
                                        "Guild Users: **${guild.members.size}**\n",
                                false
                            )
                            .build()
                    )
                    .setUsername(if (guild.name.length > 3) StringUtils.abbreviate(guild.name, 20) else "Shity name")
                    .setAvatarUrl(guild.iconUrl)
                    .build()
            )
            guildLeaveClient.close()
        } catch (ex: Exception) {
            guildLeaveClient.close()
            BoobBot.log.warn("error on Guild leave event", ex)
        }
    }
}
