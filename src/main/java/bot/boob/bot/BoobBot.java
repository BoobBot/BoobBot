package bot.boob.bot;

import bot.boob.bot.handlers.EventHandler;
import bot.boob.bot.handlers.MessageHandler;
import ch.qos.logback.classic.Logger;
import com.github.rainestormee.jdacommand.CommandHandler;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.LoggerFactory;

import static bot.boob.bot.commons.Constants.DEBUG_TOKEN;
import static bot.boob.bot.commons.Constants.IS_DEBUG;
import static bot.boob.bot.commons.Constants.TOKEN;
import static ch.qos.logback.classic.Level.DEBUG;
import static ch.qos.logback.classic.Level.INFO;

public class BoobBot {
    public static final CommandHandler commandHandler = new CommandHandler();
    public static EventWaiter waiter = new EventWaiter();
    public static Logger log = (Logger) LoggerFactory.getLogger(BoobBot.class);

    public static void main( String[] args) throws Exception {
        log.info(JDAInfo.VERSION);
        log.setLevel(INFO);
        if (args.length > 0 && args[0].contains("debug")) {
            IS_DEBUG = true;
            TOKEN = DEBUG_TOKEN;
            log.setLevel(DEBUG);
            log.warn("Running in debug");
        }

        commandHandler.registerCommands(new CommandRegistry().getCommands());
        new DefaultShardManagerBuilder()
                .setGame(Game.playing("bbhelp || bbinvite"))
                .addEventListeners(new MessageHandler(commandHandler), new EventHandler(), waiter)
                .setToken(TOKEN)
                .setShardsTotal(-1)
                .build();
    }
    }
