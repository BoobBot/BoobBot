package bot.boobbot

import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.audio.sources.pornhub.PornHubAudioSourceManager
import bot.boobbot.audio.sources.redtube.RedTubeAudioSourceManager
import bot.boobbot.flight.Command
import bot.boobbot.flight.EventWaiter
import bot.boobbot.handlers.MessageHandler
//import bot.boobbot.flight.EventWaiter
import bot.boobbot.misc.ApiServer
import bot.boobbot.misc.RequestUtil
import bot.boobbot.misc.Utils
import bot.boobbot.models.Config
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.github.natanbc.catnipvoice.CatnipVoice
import com.mewna.catnip.Catnip
import com.mewna.catnip.CatnipOptions
import com.mewna.catnip.cache.CacheFlag
import com.mewna.catnip.entity.guild.Guild
import com.mewna.catnip.entity.user.Presence
import com.mewna.catnip.shard.DiscordEvent
import com.mewna.catnip.shard.manager.DefaultShardManager
import com.mewna.catnip.util.CatnipMeta
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import de.mxro.metrics.jre.Metrics
import io.sentry.Sentry
import kotlinx.coroutines.future.await
import org.lbots.jvmclient.LBotsClient
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.set
import com.github.natanbc.catnipvoice.magma.MagmaHandler

class BoobBot {

    companion object {
        val log = LoggerFactory.getLogger(BoobBot::class.java) as Logger
        val startTime = System.currentTimeMillis()
        var autoPornChannels = 0

        public const val selfId = 285480424904327179L
        public const val inviteUrl = "https://discordapp.com/oauth2/authorize?permissions=8&client_id=285480424904327179&scope=bot"

        var isDebug = false
            private set

        lateinit var catnip: Catnip
            private set

        var isReady = false
            internal set

        var setGame = false
            internal set

        var manSetAvatar = false
            internal set

        val config = Config.load()

        val commands = HashMap<String, Command>()
        val waiter = EventWaiter()
        val requestUtil = RequestUtil()
        var sendFactory = NativeAudioSendFactory()
        var voiceHandler = MagmaHandler(sendFactory)
        val playerManager = DefaultAudioPlayerManager()
        val musicManagers = ConcurrentHashMap<String, GuildMusicManager>()
        var scheduler = Executors.newSingleThreadScheduledExecutor()
        val metrics = Metrics.create()!!

        val home: Guild?
            get() = catnip.cache().guild(config.homeGuild)

        val lbots = LBotsClient(285480424904327179, config.lbotsApiKey)

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            playerManager.registerSourceManager(PornHubAudioSourceManager())
            playerManager.registerSourceManager(RedTubeAudioSourceManager())
            playerManager.registerSourceManager(YoutubeAudioSourceManager())
            playerManager.registerSourceManager(LocalAudioSourceManager())

            val shards = config.shardCount
            val duration = Math.abs(shards * 5000)
            val currentTime = Calendar.getInstance()

            log.info("--- BoobBot.jda ---")
            log.info("Catnip: ${CatnipMeta.VERSION} | LP: ${PlayerLibrary.VERSION}")
            log.info("Launching $shards shards at an estimated ${Utils.fTime(duration.toLong())}")
            log.info("It\'s currently ${currentTime.time}")

            currentTime.add(Calendar.MILLISECOND, duration)
            log.info("Estimated full boot by ${currentTime.time}")

            isDebug = args.firstOrNull()?.contains("debug") ?: false
            val token = if (isDebug) config.debugToken else config.token

            if (isDebug) {
                log.warn("Running in debug mode")
                log.level = Level.DEBUG
            } else {
                Sentry.init(config.sentryDsn)
            }

            val opts = CatnipOptions(token)
                .chunkMembers(true)
                .presence(
                    Presence.of(
                        Presence.OnlineStatus.ONLINE,
                        Presence.Activity.of("bbhelp || bbinvite", Presence.ActivityType.PLAYING)
                    )
                )
                .shardManager(DefaultShardManager(shards))
                .cacheFlags(EnumSet.of(CacheFlag.DROP_GAME_STATUSES, CacheFlag.DROP_EMOJI))

            val handler = MessageHandler()

            catnip = Catnip.catnip(opts).connect()
            catnip.loadExtension(CatnipVoice(voiceHandler))
            catnip.on(DiscordEvent.MESSAGE_CREATE) {
                waiter.checkMessage(it)
                handler.processMessage(it)
            }

//                .setAudioSendFactory(NativeAudioSendFactory())
//                .addEventListeners(MessageHandler(), EventHandler(), waiter)

            loadCommands()
            ApiServer().startServer()
        }

        // TODO
        // -----------------------------------
        // | DON'T FORGET TO DO SQLITE STUFF |
        // -----------------------------------

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
            val manager = musicManagers.computeIfAbsent(g.id()) {
                GuildMusicManager(g.id(), playerManager.createPlayer())
            }
            val voiceExtension = catnip.extensionManager().extension(CatnipVoice::class.java)
            voiceExtension!!.setAudioProvider(g.id(), manager)
//            val audioManager = g.audioManager
//
//            if (audioManager.sendingHandler == null) {
//                audioManager.sendingHandler = manager
//            }

            return manager
        }

        public fun isAllShardsConnected(): Boolean {
            val ids = catnip.shardManager().shardIds()
            val results = mutableListOf<Boolean>()

            for (id in ids) {
                val res = catnip.shardManager().isConnected(id).toCompletableFuture().get()
                results.add(res)
            }

            return results.all { it }
        }

        public suspend fun getOnlineShards(): List<Boolean> {
            val online = BooleanArray(catnip.shardManager().shardCount())

            for (shard in catnip.shardManager().shardIds()) {
                online[shard] = catnip.shardManager().isConnected(shard).await()
            }

            return online.toList()
        }

        public suspend fun getShardLatencies(): List<Long> {
            val latency = LongArray(catnip.shardManager().shardCount())

            for (shard in catnip.shardManager().shardIds()) {
                latency[shard] = catnip.shardManager().latency(shard).await()
            }

            return latency.toList()
        }

    }

}
