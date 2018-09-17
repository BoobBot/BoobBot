package bot.boobbot;

import bot.boobbot.commons.Constants;
import bot.boobbot.handlers.EventHandler;
import bot.boobbot.handlers.MessageHandler;
import net.dv8tion.jda.bot.sharding.DefaultShardManager;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BoobBot {

    public static final Logger log = LoggerFactory.getLogger("BoobBot");

    private static boolean isDebug = false;
    private static ShardManager shardManager;
    private static final List<EventHandler> commands = new ArrayList<>(); // TODO: Change EventHandler to 'command'


    public static void main(String[] args) throws Exception {
        log.info("--- BoobBot.jda ---");
        log.info(JDAInfo.VERSION);

        isDebug = args.length > 0 && args[0].contains("debug");
        String token = isDebug ? Constants.DEBUG_TOKEN : Constants.TOKEN;

        if (isDebug) {
            log.warn("Running in debug mode");
        }

        shardManager = new DefaultShardManagerBuilder()
                .setGame(Game.playing("bbhelp | bbinvite"))
                .addEventListeners(new MessageHandler(), new EventHandler())
                .setToken(token)
                .setShardsTotal(-1)
                .build();

    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    public static List<EventHandler> getCommands() { // TODO: Change EventHandler to `command`
        return commands;
    }

    public static boolean isDebug() {
        return isDebug;
    }

}
