package bot.boobbot

import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.audio.sources.pornhub.PornHubAudioSourceManager
import bot.boobbot.audio.sources.redtube.RedTubeAudioSourceManager
import bot.boobbot.flight.Command
import bot.boobbot.misc.ApiServer
import bot.boobbot.handlers.EventHandler
import bot.boobbot.handlers.MessageHandler
import bot.boobbot.misc.Constants
import bot.boobbot.misc.EventWaiter
import bot.boobbot.misc.RequestUtil
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration
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
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors


class BoobBot : ListenerAdapter() {


    companion object {
        val log = LoggerFactory.getLogger(BoobBot::class.java) as Logger
        val startTime = System.currentTimeMillis()

        val executorPool = Executors.newSingleThreadExecutor()!!

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
        var Scheduler = Executors.newSingleThreadScheduledExecutor()!!
        val home: Guild?
            get() = shardManager.getGuildById(Constants.HOME_GUILD)

        val dotenv = dotenv {
            directory = Paths.get("").toAbsolutePath().toString()
            filename = "bb.env"
            ignoreIfMalformed = true
            ignoreIfMissing = false
        }

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            playerManager.registerSourceManager(PornHubAudioSourceManager())
            playerManager.registerSourceManager(RedTubeAudioSourceManager())
            playerManager.registerSourceManager(YoutubeAudioSourceManager())
            playerManager.registerSourceManager(LocalAudioSourceManager())
            playerManager.configuration.opusEncodingQuality = 9
            playerManager
                    .configuration.resamplingQuality = AudioConfiguration.ResamplingQuality.MEDIUM // lets destroy it a lil // don't destroy CPU

            log.info("--- BoobBot.jda ---")
            log.info(JDAInfo.VERSION)

            isDebug = args.isNotEmpty() && args[0].contains("debug")
            val token = if (isDebug) Constants.DEBUG_TOKEN else Constants.TOKEN
            if (!isDebug) {
                Sentry.init(Constants.SENTRY_DSN)
            }
            if (isDebug) {
                log.warn("Running in debug mode")
                log.level = Level.DEBUG
            }

            shardManager = DefaultShardManagerBuilder()
                    .setGame(Game.playing("bbhelp | bbinvite"))
                    .setAudioSendFactory(NativeAudioSendFactory())
                    .addEventListeners(BoobBot(), MessageHandler(), EventHandler(), waiter)
                    .setToken(token)
                    .setShardsTotal(-1)
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

            log.info("Successfully loaded " + commands.size + " commands!")
            // TODO: Eval
        }

        fun getMusicManager(g: Guild): GuildMusicManager {
            val manager = musicManagers.computeIfAbsent(g.idLong) { GuildMusicManager(g.idLong, playerManager.createPlayer()) }
            val audioManager = g.audioManager

            if (audioManager.sendingHandler == null) {
                audioManager.sendingHandler = manager
            }

            return manager
        }

    }

}

