package bot.boobbot

import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.audio.sources.pornhub.PornHubAudioSourceManager
import bot.boobbot.audio.sources.redtube.RedTubeAudioSourceManager
import bot.boobbot.flight.EventWaiter
import bot.boobbot.flight.ExecutableCommand
import bot.boobbot.flight.Indexer
import bot.boobbot.handlers.EventHandler
import bot.boobbot.handlers.MessageHandler
import bot.boobbot.misc.ApiServer
import bot.boobbot.misc.Database
import bot.boobbot.misc.RequestUtil
import bot.boobbot.misc.Utils
import bot.boobbot.models.Config
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import de.mxro.metrics.jre.Metrics
import io.sentry.Sentry
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.bot.sharding.ShardManager
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDAInfo
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.utils.cache.CacheFlag
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.collections.set

class BoobBot {

    companion object {
        val log = LoggerFactory.getLogger(BoobBot::class.java) as Logger
        val startTime = System.currentTimeMillis()
        var autoPornChannels = 0

        public lateinit var VERSION: String
            private set

        public const val selfId = 285480424904327179L
        public const val inviteUrl =
            "https://discordapp.com/oauth2/authorize?permissions=8&client_id=285480424904327179&scope=bot"

        var isDebug = false
            private set

        lateinit var shardManager: ShardManager
            private set

        var isReady = false
            internal set

        var setGame = false
            internal set

        var manSetAvatar = false
            internal set

        val config = Config.load()
        val database = Database()

        val commands = HashMap<String, ExecutableCommand>()
        val waiter = EventWaiter()
        val requestUtil = RequestUtil()
        val playerManager = DefaultAudioPlayerManager()
        val musicManagers = ConcurrentHashMap<Long, GuildMusicManager>()
        var scheduler = Executors.newSingleThreadScheduledExecutor()!!
        val metrics = Metrics.create()!!

        val home: Guild?
            get() = shardManager.getGuildById(config.homeGuild)


        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            getVersion()

            playerManager.registerSourceManager(PornHubAudioSourceManager())
            playerManager.registerSourceManager(RedTubeAudioSourceManager())
            playerManager.registerSourceManager(YoutubeAudioSourceManager())
            playerManager.registerSourceManager(LocalAudioSourceManager())

            isDebug = args.firstOrNull()?.contains("debug") ?: false
            val shardCount = if (isDebug) 2 else config.shardCount
            val duration = Math.abs(shardCount * 5000)
            val currentTime = Calendar.getInstance()

            log.info("--- BoobBot (Revision $VERSION) ---")
            log.info("JDA: ${JDAInfo.VERSION} | LP: ${PlayerLibrary.VERSION}")
            log.info("Launching $shardCount shards at an estimated ${Utils.fTime(duration.toLong())}")
            log.info("It\'s currently ${currentTime.time}")

            currentTime.add(Calendar.MILLISECOND, duration)
            log.info("Estimated full boot by ${currentTime.time}")

            val token = if (isDebug) config.debugToken else config.token

            val sessionLimit = getRemainingSessionCount(token)
            BoobBot.log.info("-- REMAINING LOGINS AVAILABLE: $sessionLimit")
            log.level = Level.DEBUG

            if (isDebug) {
                log.warn("Running in debug mode")
            } else {
                Sentry.init(config.sentryDsn)
            }

            val jdaHttp = OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .build()

            shardManager = DefaultShardManagerBuilder()
                .setToken(token)
                .setShardsTotal(shardCount)
                .setGame(Game.playing("Booting...."))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(waiter, MessageHandler(), EventHandler())
                .setAudioSendFactory(NativeAudioSendFactory())
                .setHttpClient(jdaHttp)
                .setDisabledCacheFlags(EnumSet.of(CacheFlag.EMOTE, CacheFlag.GAME))
                .build()

            indexCommands()
            ApiServer().startServer()
        }

        private fun indexCommands() {
            val indexer = Indexer("bot.boobbot.commands")

            for (cmd in indexer.getCommands()) {
                commands[cmd.name] = cmd
            }

            log.info("Successfully loaded ${commands.size} commands!")
        }

        fun getMusicManager(g: Guild): GuildMusicManager {
            val manager =
                musicManagers.computeIfAbsent(g.idLong) { GuildMusicManager(g.idLong, playerManager.createPlayer()) }
            val audioManager = g.audioManager

            if (audioManager.sendingHandler == null) {
                audioManager.sendingHandler = manager
            }

            return manager
        }

        fun getVersion() {
            val revisionProc = Runtime.getRuntime().exec("git rev-parse --short HEAD")
            VERSION = Utils.readAll(revisionProc.inputStream)
        }

        public fun isAllShardsConnected(): Boolean {
            return shardManager.shards.all { it.status == JDA.Status.CONNECTED }
        }

        public fun getOnlineShards(): List<JDA> {
            return shardManager.shards.filter { it.status == JDA.Status.CONNECTED }
        }

        public fun getShardLatencies(): List<Long> {
            return shardManager.shards.map { it.ping }
        }

        public fun getRemainingSessionCount(token: String): Int {
            return try {
                val url = URL("https://discordapp.com/api/gateway/bot")
                val connection = url.openConnection()
                connection.setRequestProperty("Authorization", "Bot $token")

                val res = Utils.readAll(connection.getInputStream())
                val json = JSONObject(res)

                json.getJSONObject("session_start_limit").getInt("remaining")
            } catch (e: Exception) {
                -1
            }
        }

    }

}
