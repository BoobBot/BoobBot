package bot.boob.bot.commons.apis;

import bot.boob.bot.commons.Misc;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class oBoobs {
    private static final OkHttpClient client = new OkHttpClient();

    public static String getBoobs() throws Exception {
        Request request = new Request.Builder()
                .url("http://api.oboobs.ru/boobs/0/1/random")
                .build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return "http://media.oboobs.ru/"+
                    new JSONObject(Objects.requireNonNull(responseBody).string()
                            .replace("[","")
                            .replace("]",""))
                            .get("preview")
                            .toString();
        }
    }
}
