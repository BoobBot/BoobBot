package bot.boobbot.misc;

import bot.boobbot.BoobBot;
import bot.boobbot.flight.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import okhttp3.Headers;
import okhttp3.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

    public static BufferedImage downloadAvatar(String url) {
        Response res = BoobBot.getRequestUtil().get(url, Headers.of()).block();

        if (res == null || res.body() == null) {
            return null;
        }

        try {
            return ImageIO.read(res.body().byteStream());
        } catch (IOException e) {
            return null;
        }
    }

}
