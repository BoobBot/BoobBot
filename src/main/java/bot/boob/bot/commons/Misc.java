package bot.boob.bot.commons;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Tom on 10/4/2017.
 */
public class Misc {

    public static final String[] UA = {
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"
    };


    public static String now() {
        DateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy, h:mm:ss a");
        return dateFormat.format(new Date());
    }

    public static BufferedImage getAvatar(User user) {
        BufferedImage ava = null;
        try {
            URL userAva = new URL(user.getEffectiveAvatarUrl());
            URLConnection connection = userAva.openConnection();
            connection.setRequestProperty(UA[0], UA[1]);
            connection.connect();
            ava = ImageIO.read(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ava;
    }

    public static WebhookClient webhookClient(String url) {
        return new WebhookClientBuilder(url).build();
    }

}
