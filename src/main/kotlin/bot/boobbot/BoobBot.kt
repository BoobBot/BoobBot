package bot.boobbot

import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.audio.sources.pornhub.PornHubAudioSourceManager
import bot.boobbot.audio.sources.redtube.RedTubeAudioSourceManager
import bot.boobbot.entities.framework.EventWaiter
import bot.boobbot.entities.internals.*
import bot.boobbot.entities.misc.*
import bot.boobbot.utils.RequestUtil
import bot.boobbot.utils.Utils
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.fasterxml.jackson.core.JsonParseException
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer
import de.mxro.metrics.jre.Metrics
import io.ktor.util.KtorExperimentalAPI
import io.sentry.connection.ConnectionException
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.entities.ApplicationInfo
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.exceptions.ContextException
import net.dv8tion.jda.api.requests.RestAction
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledExecutorService

class BoobBot {
    companion object {

        val log = LoggerFactory.getLogger(BoobBot::class.java) as Logger
        val startTime = System.currentTimeMillis()

        private lateinit var application: ApplicationInfo
        val selfId: Long
            get() = application.idLong
        lateinit var inviteUrl: String

        var isDebug = false
            private set

        var logCom = false
            private set

        lateinit var shardManager: CustomShardManager
            private set

        var isReady = false
            internal set

        val defaultPrefix by lazy {
            if (isDebug) "!bb" else "bb"
        }

        val config = Config.load()
        val database = Database()

        val commands = CommandRegistry()
        val slashCommands = SlashCommandRegistry()
        val UserContextCommands = UserContextCommandRegistry()
        val waiter = EventWaiter()
        val requestUtil = RequestUtil()
        val playerManager = DefaultAudioPlayerManager().also {
            it.configuration.setFrameBufferFactory(::NonAllocatingAudioFrameBuffer)
        }
        val musicManagers = ConcurrentHashMap<Long, GuildMusicManager>()
        val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        val metrics = Metrics.create()!!
        val pApi = PatreonAPI(config.PATREON_KEY)

        @KtorExperimentalAPI
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            print(UserContextCommands)
            isDebug = args.any { it == "--debug" }
            logCom = args.any { it == "--comlog" }
            val shardCount = if (isDebug) 1 else config.SHARD_TOTAL
            val token = if (isDebug) config.DEBUG_TOKEN else config.TOKEN

            log.info("--- BoobBot (Revision {}) ---", Utils.version)
            log.info(
                "JDA: {} | LP: {} | {} shards | {} logins",
                JDAInfo.VERSION,
                PlayerLibrary.VERSION,
                shardCount,
                CustomShardManager.retrieveRemainingSessionCount(token)
            )

            shardManager = CustomShardManager.create(token, shardCount)
            application = shardManager.retrieveApplicationInfo().complete()
            inviteUrl = "https://discordapp.com/oauth2/authorize?permissions=8&client_id=$selfId&scope=bot"

            if (isDebug) {
                log.level = Level.DEBUG
                log.warn("Running in debug mode")
            } else {
                CustomSentryClient.create(config.SENTRY_DSN)
                    .ignore(
                        ContextException::class.java,
                        ConnectionException::class.java,
                        SocketException::class.java,
                        SocketTimeoutException::class.java,
                        IOException::class.java,
                        JsonParseException::class.java,
                        RejectedExecutionException::class.java,
                        ConnectException::class.java
                    )
            }

            setupAudioSystem()
            RestAction.setPassContext(false)
            ApiServer().startServer()
        }

        private fun setupAudioSystem() {
            playerManager.registerSourceManager(PornHubAudioSourceManager())
            playerManager.registerSourceManager(RedTubeAudioSourceManager())
            playerManager.registerSourceManager(YoutubeAudioSourceManager())
            playerManager.registerSourceManager(LocalAudioSourceManager())
        }

        fun getMusicManager(g: Guild): GuildMusicManager {
            val audioManager = g.audioManager
            val manager = musicManagers.computeIfAbsent(g.idLong) {
                GuildMusicManager(it, playerManager.createPlayer())
            }

            if (audioManager.sendingHandler == null) {
                audioManager.sendingHandler = manager
            }

            return manager
        }
    }

}
