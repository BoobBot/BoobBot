package bot.boobbot;

import bot.boobbot.flight.Command;
import bot.boobbot.handlers.EventHandler;
import bot.boobbot.handlers.MessageHandler;
import bot.boobbot.misc.Constants;
import bot.boobbot.misc.EventWaiter;
import bot.boobbot.misc.RequestUtil;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.sentry.Sentry;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static bot.boobbot.misc.Constants.SENTRY_DSN;


public class BoobBot {

    public static Logger log = (Logger) LoggerFactory.getLogger(BoobBot.class);
    private static boolean isDebug = false;
    private static ShardManager shardManager;
    private static final Map<String, Command> commands = new HashMap<>();
    private static final EventWaiter waiter = new EventWaiter();
    private static final RequestUtil requestUtil = new RequestUtil();


    public static void main(String[] args) throws Exception {
        Sentry.init(SENTRY_DSN);
        log.info("--- BoobBot.jda ---");
        log.info(JDAInfo.VERSION);

        isDebug = args.length > 0 && args[0].contains("debug");
        String token = isDebug ? Constants.DEBUG_TOKEN : Constants.TOKEN;

        if (isDebug) {
            log.warn("Running in debug mode");
            log.setLevel(Level.DEBUG);
        }

        shardManager = new DefaultShardManagerBuilder()
                .setGame(Game.playing("bbhelp | bbinvite"))
                .addEventListeners(new MessageHandler(), new EventHandler(), waiter)
                .setToken(token)
                .setShardsTotal(-1)
                .build();
    }

    private static void loadCommands() {
        Reflections reflections = new Reflections("bot.boobbot.commands");

        reflections.getSubTypesOf(Command.class).forEach(command -> {
            if (Modifier.isAbstract(command.getModifiers()) || command.isInterface()) {
                return;
            }

            try {
                Command cmd = command.newInstance();

                if (cmd.getProperties() == null) {
                    log.warn("Command `" + cmd.getName() + "` is missing CommandProperties annotation. Will not load.");
                    return;
                }

                commands.put(cmd.getName(), cmd);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Failed to load command `" + command.getSimpleName() + "`", e);
            }
        });

        log.info("Successfully loaded " + commands.size() + " commands!");
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    public static Map<String, Command> getCommands() {
        return commands;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static EventWaiter getWaiter() {
        return waiter;
    }

    public static RequestUtil getRequestUtil() {
        return requestUtil;
    }

}
