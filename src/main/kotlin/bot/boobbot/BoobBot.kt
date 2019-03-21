package bot.boobbot

import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.audio.sources.pornhub.PornHubAudioSourceManager
import bot.boobbot.audio.sources.redtube.RedTubeAudioSourceManager
import bot.boobbot.flight.Command
import bot.boobbot.handlers.EventHandler
import bot.boobbot.handlers.MessageHandler
import bot.boobbot.misc.*
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import de.mxro.metrics.jre.Metrics
import io.github.cdimascio.dotenv.dotenv
import io.sentry.Sentry
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.bot.sharding.ShardManager
import net.dv8tion.jda.core.JDAInfo
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.hooks.ListenerAdapter
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.lbots.jvmclient.LBotsClient
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors


class BoobBot : ListenerAdapter() {

    companion object {
        var autoPornChannels = 0
        val log = LoggerFactory.getLogger(BoobBot::class.java) as Logger
        val startTime = System.currentTimeMillis()

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
        val metrics = Metrics.create()!!
        val commands = HashMap<String, Command>()
        val waiter = EventWaiter()
        val requestUtil = RequestUtil()
        val playerManager = DefaultAudioPlayerManager()
        val musicManagers = ConcurrentHashMap<Long, GuildMusicManager>()
        //val shitUsers = ConcurrentHashMap<Long, Int>()
        var Scheduler = Executors.newSingleThreadScheduledExecutor()!!
        val dotenv = dotenv {
            directory = Paths.get("").toAbsolutePath().toString()
            filename = "bb.env"
            ignoreIfMalformed = true
            ignoreIfMissing = false
        }
        val home: Guild?
            get() = shardManager.getGuildById(Constants.HOME_GUILD)

        val lbots = LBotsClient(285480424904327179, Constants.LBOTS_API_KEY)

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            playerManager.registerSourceManager(PornHubAudioSourceManager())
            playerManager.registerSourceManager(RedTubeAudioSourceManager())
            playerManager.registerSourceManager(YoutubeAudioSourceManager())
            playerManager.registerSourceManager(LocalAudioSourceManager())

            log.info("--- BoobBot.jda ---")
            log.info("jda version: ${JDAInfo.VERSION}")
            val duration = Constants.SHARD_COUNT.toString().toInt() * 5000
            val now = Calendar.getInstance()
            val targetTime = now.clone() as Calendar
            targetTime.add(Calendar.MILLISECOND, duration)
            log.info("Launching ${Constants.SHARD_COUNT} shards at an estimated ${Utils.fTime(duration.toLong())}\nEstimated full boot by ${targetTime.time}\nIt\'s currently ${now.time}")
            log.info("Estimated full boot by ${targetTime.time}")
            log.info("It\'s currently ${now.time}")
            isDebug = args.firstOrNull()?.contains("debug") ?: false
            val token = if (isDebug) Constants.DEBUG_TOKEN else Constants.TOKEN
            if (!isDebug) {
                Sentry.init(Constants.SENTRY_DSN)
            }
            if (isDebug) {
                log.warn("Running in debug mode")
                log.level = Level.DEBUG
            }

            val jdaHttpClient = OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_1_1))

            shardManager = DefaultShardManagerBuilder()
                .setGame(Game.playing("bbhelp | bbinvite"))
                .setAudioSendFactory(NativeAudioSendFactory())
                .addEventListeners(BoobBot(), MessageHandler(), EventHandler(), waiter)
                .setToken(token)
                .setShardsTotal(Constants.SHARD_COUNT.toString().toInt())
                .setHttpClientBuilder(jdaHttpClient)
                .build()

            loadCommands()
            ApiServer().startServer()
        }

        private fun loadCommands() {
            val reflections = Reflections("bot.boobbot.commands")

            reflections.getSubTypesOf(Command::class.java).forEach { command ->
                if (Modifier.isAbstract(command.modifiers) || command.isInterface) {
                    return@forEach
                }

                try {
                    val cmd = command.getDeclaredConstructor().newInstance()
                    if (!cmd.hasProperties) {
                        return@forEach log.warn("Command `${cmd.name}` is missing CommandProperties annotation. Will not load.")
                    }

                    commands[cmd.name] = cmd
                } catch (e: InstantiationException) {
                    log.error("Failed to load command `${command.simpleName}`", e)
                } catch (e: IllegalAccessException) {
                    log.error("Failed to load command `${command.simpleName}`", e)
                }
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

    }

}

