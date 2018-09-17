package bot.boobbot.misc;

import bot.boobbot.BoobBot;
import bot.boobbot.flight.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {

    public static Command getCommand(String commandName) {
        Map<String, Command> commands = BoobBot.getCommands();

        if (commands.containsKey(commandName)) {
            return commands.get(commandName);
        }

        return commands.values()
                .stream()
                .filter(c -> Arrays.asList(c.getProperties().aliases()).contains(commandName))
                .findFirst()
                .orElse(null);
    }

    public static List<Permission> getMissingPermissions(Member m, TextChannel channel, Permission... permissions) {
        List<Permission> missing = new ArrayList<>();
        List<Permission> current = m.getPermissions(channel);

        for (Permission p : permissions) {
            if (!current.contains(p)) {
                missing.add(p);
            }
        }

        return missing;
    }

}
