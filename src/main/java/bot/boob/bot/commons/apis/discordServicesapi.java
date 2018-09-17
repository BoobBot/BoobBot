package bot.boob.bot.commons.apis;

import java.net.URL;

public class discordServicesapi {
    public static URL getMagikUrl(String url) throws Exception {
        return new URL("https://discord.services/api/magik/?url=" + url);
    }
}
