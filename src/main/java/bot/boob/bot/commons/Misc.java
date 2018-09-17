package bot.boob.bot.commons;

import com.google.common.collect.Lists;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * Created by Tom on 10/4/2017.
 */
public class Misc {

    public static final String[] UA = {
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"
    };

    public static Proxy getProxy() {
        List<String> ps =
                Lists.newArrayList(
                        "5.231.237.168:3213",
                        "94.249.224.97:2543",
                        "185.164.57.91:4012",
                        "185.164.57.144:9749",
                        "185.164.57.70:5756");
        Random rand = new Random();
        String proxy = ps.get(rand.nextInt(ps.size()));
        String[] parts = proxy.split(":", 2);
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(parts[0], Integer.parseInt(parts[1])));
    }

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
