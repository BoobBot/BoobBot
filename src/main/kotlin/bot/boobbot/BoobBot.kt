package bot.boobbot

import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.audio.sources.pornhub.PornHubAudioSourceManager
import bot.boobbot.audio.sources.redtube.RedTubeAudioSourceManager
import bot.boobbot.flight.EventWaiter
import bot.boobbot.internals.CommandRegistry
import bot.boobbot.internals.CustomSentryClient
import bot.boobbot.internals.CustomShardManager
import bot.boobbot.misc.*
import bot.boobbot.models.Config
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import de.mxro.metrics.jre.Metrics
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.entities.ApplicationInfo
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.exceptions.ContextException
import org.slf4j.LoggerFactory
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.math.abs

class BoobBot {
    companion object {
        val log = LoggerFactory.getLogger(BoobBot::class.java) as Logger
        val startTime = System.currentTimeMillis()
        const val VERSION = "1.3.420.69"

        lateinit var application: ApplicationInfo
        val selfId: Long
            get() = application.idLong
        lateinit var inviteUrl: String

        var isDebug = false
            private set

        lateinit var shardManager: CustomShardManager
            private set

        var isReady = false
            internal set

        val defaultPrefix: String
            get() = if (isDebug) "!bb" else "bb"

        var manSetAvatar = false
            internal set

        val config = Config.load()
        val database = Database()

        val commands = CommandRegistry.load()
        val waiter = EventWaiter()
        val requestUtil = RequestUtil()
        val playerManager = DefaultAudioPlayerManager()
        val musicManagers = ConcurrentHashMap<Long, GuildMusicManager>()
        var scheduler = Executors.newSingleThreadScheduledExecutor()!!
        val metrics = Metrics.create()!!

        /* Experimental as fuck */
        val pApi = PatreonAPI(config.patreonApiKey)


        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            playerManager.registerSourceManager(PornHubAudioSourceManager())
            playerManager.registerSourceManager(RedTubeAudioSourceManager())
            playerManager.registerSourceManager(YoutubeAudioSourceManager())
            playerManager.registerSourceManager(LocalAudioSourceManager())

            isDebug = args.firstOrNull()?.contains("debug") ?: false
            val shardCount = if (isDebug) 1 else config.shardCount
            val duration = abs(shardCount * 5000)
            val currentTime = Calendar.getInstance()

            log.info("--- BoobBot (Revision $VERSION) ---")
            log.info("JDA: ${JDAInfo.VERSION} | LP: ${PlayerLibrary.VERSION}")
            log.info("Launching $shardCount shards at an estimated ${Utils.fTime(duration.toLong())}")
            log.info("It\'s currently ${currentTime.time}")

            currentTime.add(Calendar.MILLISECOND, duration)
            log.info("Estimated full boot by ${currentTime.time}")

            val token = if (isDebug) config.debugToken else config.token
            shardManager = CustomShardManager.create(token, shardCount)
            application = shardManager.retrieveApplicationInfo().complete()
            inviteUrl = "https://discordapp.com/oauth2/authorize?permissions=8&client_id=$selfId&scope=bot"

            log.info("-- REMAINING LOGINS AVAILABLE: ${shardManager.retrieveRemainingSessionCount()}")
            log.level = Level.DEBUG

            if (isDebug) {
                log.warn("Running in debug mode")
            } else {
                CustomSentryClient.create(config.sentryDsn)
                    .ignore(
                        ContextException::class.java,
                        SocketException::class.java,
                        SocketTimeoutException::class.java
                    )
            }

            ApiServer().startServer()
        }

        fun getMusicManager(g: Guild): GuildMusicManager {
            val audioManager = g.audioManager
            val manager = musicManagers.computeIfAbsent(g.idLong) {
                GuildMusicManager(g.idLong, playerManager.createPlayer())
            }

            if (audioManager.sendingHandler == null) {
                audioManager.sendingHandler = manager
            }

            return manager
        }
    }

}
