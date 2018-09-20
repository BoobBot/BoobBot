package bot.boobbot

import bot.boobbot.flight.Command
import bot.boobbot.handlers.EventHandler
import bot.boobbot.handlers.MessageHandler
import bot.boobbot.misc.Constants
import bot.boobbot.misc.EventWaiter
import bot.boobbot.misc.RequestUtil
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import io.sentry.Sentry
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.bot.sharding.ShardManager
import net.dv8tion.jda.core.JDAInfo
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import java.util.*


class BoobBot {

    companion object {
        var log = LoggerFactory.getLogger(BoobBot::class.java) as Logger

        var isDebug = false
            private set

        lateinit var shardManager: ShardManager
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
                    .addEventListeners(MessageHandler(), EventHandler(), waiter)
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
