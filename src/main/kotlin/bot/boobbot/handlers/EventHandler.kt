//package bot.boobbot.handlers
//
//import bot.boobbot.BoobBot
//import bot.boobbot.BoobBot.Companion.setGame
//import bot.boobbot.misc.AutoPorn
//import bot.boobbot.misc.Formats
//import bot.boobbot.misc.Utils
//import com.mewna.catnip.entity.misc.Ready
//import com.mewna.catnip.entity.user.User
//import de.mxro.metrics.jre.Metrics
//import org.apache.commons.lang3.StringUtils
//import java.awt.Color
//import java.time.Instant.now
//import java.util.concurrent.TimeUnit
//
//
//class EventHandler {
//    var self: User? = null // just to hold self for discon webhooks
//    var readyCount = 0
//
//    fun onReady(event: Ready) {
//        BoobBot.metrics.record(Metrics.happened("Ready"))
//        BoobBot.log.info("Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.ping}ms, Status: ${event.jda.status}")
//        if (!BoobBot.isReady) {
//            readyCount++
//        }
//        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
//        readyClient.send(
//            WebhookMessageBuilder().addEmbeds(
//                EmbedBuilder().color(Color.magenta)
//                    .author(
//                        event.jda.selfUser.name,
//                        event.jda.selfUser.effectiveAvatarUrl,
//                        event.jda.selfUser.effectiveAvatarUrl
//                    ).title(
//                        "```Ready on shard: ${event.jda.shardInfo.shardId}, Ping: ${event.jda.ping}ms, Status: ${event.jda.status}```",
//                        event.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR)
//                    )
//                    .timestamp(now()).build()
//            ).setUsername(event.jda.selfUser.name).setAvatarUrl(event.jda.selfUser.effectiveAvatarUrl)
//                .build()
//        )
//        if (readyCount == Constants.SHARD_COUNT.toString().toInt()-1 && !BoobBot.isReady) {
//            BoobBot.isReady = true
//            if (!BoobBot.isDebug) { // dont need this is testing
//                BoobBot.Scheduler.scheduleAtFixedRate(Utils.auto(), 1, 2, TimeUnit.HOURS)
//                BoobBot.Scheduler.scheduleAtFixedRate(AutoPorn.auto(), 1, 5, TimeUnit.MINUTES)
//                BoobBot.Scheduler.scheduleAtFixedRate(Utils.autoFix(), 1, 30, TimeUnit.SECONDS)
//            }
//            self = event.jda.selfUser // set
//            BoobBot.log.info(Formats.getReadyFormat())
//            readyClient.send(
//                WebhookMessageBuilder().setContent("Ready").addEmbeds(
//                    EmbedBuilder().color(Color.magenta)
//                        .author(
//                            event.jda.selfUser.name,
//                            event.jda.selfUser.effectiveAvatarUrl,
//                            event.jda.selfUser.effectiveAvatarUrl
//                        )
//                        .title(
//                            "${event.jda.selfUser.name} Fully Ready",
//                            event.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR)
//                        )
//                        .setThumbnail(event.jda.selfUser.effectiveAvatarUrl).addField(
//                            "Ready info",
//                            "``` ${Formats.getReadyFormat()}```",
//                            false
//                        )
//                        .timestamp(now())
//                        .build()
//                ).setUsername(event.jda.selfUser.name).setAvatarUrl(event.jda.selfUser.effectiveAvatarUrl)
//                    .build()
//            )
//            readyClient.close()
//            if (!BoobBot.isDebug) {
//                BoobBot.log.info("Response for  lbots stats update was ${BoobBot.lbots.updateStats(BoobBot.shardManager.guilds.size)}")
//            }
//        }
//        readyClient.close()
//    }
//
//
//    fun onReconnect(event: Reconnect) {
//        BoobBot.metrics.record(Metrics.happened("Reconnected"))
//        BoobBot.log.info("Reconnected on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}")
//        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
//        try {
//            readyClient.send(
//                WebhookMessageBuilder().addEmbeds(
//                    EmbedBuilder().color(Color.green)
//                        .author(
//                            self?.name,
//                            self?.effectiveAvatarUrl,
//                            self?.effectiveAvatarUrl
//                        ).title("```Reconnected on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}```")
//                        .timestamp(now()).build()
//                ).setUsername(self?.name).setAvatarUrl(self?.effectiveAvatarUrl)
//                    .build()
//            )
//            readyClient.close()
//        } catch (ex: Exception) {
//            readyClient.close()
//            BoobBot.log.warn("error on reconnected event", ex)
//        }
//    }
//
//    fun onResume(event: ResumedEvent?) {
//        BoobBot.metrics.record(Metrics.happened("Resumed"))
//        BoobBot.log.info("Resumed on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}")
//        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
//        try {
//            readyClient.send(
//                WebhookMessageBuilder().addEmbeds(
//                    EmbedBuilder().color(Color.green)
//                        .author(
//                            self?.name,
//                            self?.effectiveAvatarUrl,
//                            self?.effectiveAvatarUrl
//                        ).title("```Resumed on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}```")
//                        .timestamp(now()).build()
//                ).setUsername(self?.name).setAvatarUrl(self?.effectiveAvatarUrl)
//                    .build()
//            )
//            readyClient.close()
//        } catch (ex: Exception) {
//            readyClient.close()
//            BoobBot.log.warn("error on resumed event", ex)
//        }
//    }
//
//    fun onDisconnect(event: DisconnectEvent?) {
//        BoobBot.metrics.record(Metrics.happened("Disconnect"))
//        BoobBot.log.info("Disconnect on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}")
//        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
//        try {
//            readyClient.send(
//                WebhookMessageBuilder().addEmbeds(
//                    EmbedBuilder().color(Color.green)
//                        .author(
//                            self?.name,
//                            self?.effectiveAvatarUrl,
//                            self?.effectiveAvatarUrl
//                        ).title("```Disconnect on shard: ${event?.jda?.shardInfo?.shardId}, Status: ${event?.jda?.status}```")
//                        .timestamp(now()).build()
//                ).setUsername(self?.name).setAvatarUrl(self?.effectiveAvatarUrl)
//                    .build()
//            )
//            readyClient.close()
//        } catch (ex: Exception) {
//            readyClient.close()
//            BoobBot.log.warn("error on Disconnect event", ex)
//        }
//    }
//
//    fun onGuildJoin(event: GuildJoinEvent?) {
//        BoobBot.metrics.record(Metrics.happened("GuildJoin"))
//        if (!BoobBot.isReady) {
//            return
//        }
//        val jda = event!!.jda
//        val guild = event.guild
//        if (!setGame) {
//            event.jda.asBot().shardManager.setGame(Game.playing("bbhelp || bbinvite"))
//        }
//        BoobBot.log.info("New Guild Joined ${guild.name}(${guild.id})")
//        val em = EmbedBuilder()
//            .color(Color.green)
//            .author(guild.name, guild.iconUrl, guild.iconUrl)
//            .title("Joined ${guild.name}")
//            .setThumbnail(guild.iconUrl)
//            .description("Guild info")
//            .addField(
//                Formats.info("info"),
//                "**${guild.jda.shardInfo}**\n" +
//                        "Guilds: **${jda.asBot().shardManager.guilds.size}**\n" +
//                        "Owner: **${guild.owner.effectiveName}**\n" +
//                        "Guild Users: **${guild.members.size}**\n",
//                false
//            )
//            .timestamp(now())
//            .build()
//        val guildJoinClient = WebhookClientBuilder(Constants.GJLOG_WEBHOOK).build()
//        try {
//            guildJoinClient.send(
//                WebhookMessageBuilder()
//                    .addEmbeds(em)
//                    .setUsername(if (guild.name.length > 3) StringUtils.abbreviate(guild.name, 20) else "Shity name")
//                    .setAvatarUrl(guild.iconUrl)
//                    .build()
//            )
//            guildJoinClient.close()
//        } catch (ex: java.lang.Exception) {
//            guildJoinClient.close()
//            BoobBot.log.warn("error on Guild join event", ex)
//        }
//        BoobBot.log.info("Response for  lbots stats update was ${BoobBot.lbots.updateStats(BoobBot.shardManager.guilds.size)}")
//    }
//
//    fun onGuildLeave(event: GuildLeaveEvent?) {
//        BoobBot.metrics.record(Metrics.happened("GuildLeave"))
//        if (!BoobBot.isReady) {
//            return
//        }
//        val jda = event!!.jda
//        val guild = event.guild
//        if (!setGame) {
//            event.jda.asBot().shardManager.setGame(Game.playing("bbhelp || bbinvite"))
//        }
//        BoobBot.log.info("Guild left ${guild.name}(${guild.id})")
//        val guildLeaveClient = WebhookClientBuilder(Constants.GLLOG_WEBHOOK).build()
//        try {
//            guildLeaveClient.send(
//                WebhookMessageBuilder()
//                    .addEmbeds(
//                        EmbedBuilder()
//                            .color(Color.red)
//                            .author(guild.name, guild.iconUrl, guild.iconUrl)
//                            .title("Left ${guild.name}")
//                            .setThumbnail(guild.iconUrl)
//                            .description("Guild info")
//                            .addField(
//                                Formats.info("info"),
//                                "**${guild.jda.shardInfo}**\n" +
//                                        "Guilds: **${jda.asBot().shardManager.guilds.size}**\n" +
//                                        "Owner: **${guild.owner.effectiveName}**\n" +
//                                        "Guild Users: **${guild.members.size}**\n",
//                                false
//                            )
//                            .build()
//                    )
//                    .setUsername(if (guild.name.length > 3) StringUtils.abbreviate(guild.name, 20) else "Shity name")
//                    .setAvatarUrl(guild.iconUrl)
//                    .build()
//            )
//            guildLeaveClient.close()
//        } catch (ex: Exception) {
//            guildLeaveClient.close()
//            BoobBot.log.warn("error on Guild leave event", ex)
//        }
//        BoobBot.log.info("Response for  lbots stats update was ${BoobBot.lbots.updateStats(BoobBot.shardManager.guilds.size)}")
//    }
//}
