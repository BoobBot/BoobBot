package bot.boobbot

import bot.boobbot.flight.Command
import bot.boobbot.handlers.EventHandler
import bot.boobbot.handlers.MessageHandler
import bot.boobbot.misc.Constants
import bot.boobbot.misc.EventWaiter
import bot.boobbot.misc.Formats.Companion.getReadyFormat
import bot.boobbot.misc.GuildMusicManager
import bot.boobbot.misc.RequestUtil
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import io.ktor.server.netty.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.server.engine.*
import io.sentry.Sentry
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.bot.sharding.ShardManager
import net.dv8tion.jda.core.JDAInfo
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class BoobBot : ListenerAdapter() {

    override fun onReady(event: ReadyEvent) {
        if (shardManager.shardsRunning == shardManager.shardsTotal && !isReady) {
            isReady = true
            // health check for status page
            embeddedServer(Netty, 8008) {
                routing {
                    get("/health") {
                        call.respondText("{health: ok, ping: ${shardManager.averagePing}}", ContentType.Application.Json)
                    }
                }
            }.start(wait = false)
            log.info(getReadyFormat(event.jda, home))
        }
    }

    companion object {
        lateinit var playerManager: AudioPlayerManager
        lateinit var musicManagers: Map<String, GuildMusicManager>

        val log = LoggerFactory.getLogger(BoobBot::class.java) as Logger
        val startTime = System.currentTimeMillis()

        var isDebug = false
            private set

        lateinit var shardManager: ShardManager
            private set

        var isReady = false
            private set

        private val commands = HashMap<String, Command>()
        val waiter = EventWaiter()
        val requestUtil = RequestUtil()

        val home: Guild?
            get() = shardManager.getGuildById(Constants.HOME_GUILD)


        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            Sentry.init(Constants.SENTRY_DSN)
            playerManager = DefaultAudioPlayerManager()
            AudioSourceManagers.registerRemoteSources(playerManager)
            playerManager.registerSourceManager(YoutubeAudioSourceManager())
           // playerManager.registerSourceManager(PornHubAudioSourceManager()) //TODO add this stuff
            //playerManager.registerSourceManager(RedTubeAudioSourceManager())
            playerManager.configuration.opusEncodingQuality = 10
            playerManager
                    .configuration.resamplingQuality = AudioConfiguration.ResamplingQuality.HIGH

            musicManagers = ConcurrentHashMap()
            log.info("--- BoobBot.jda ---")
            log.info(JDAInfo.VERSION)

            isDebug = args.isNotEmpty() && args[0].contains("debug")
            val token = if (isDebug) Constants.DEBUG_TOKEN else Constants.TOKEN

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

        fun getCommands(): Map<String, Command> {
            return commands
        }

    }

}
