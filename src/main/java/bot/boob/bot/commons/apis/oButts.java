package bot.boob.bot.commons.apis;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class oButts {
    private static final OkHttpClient client = new OkHttpClient();

    public static String getAss() throws Exception {
        Request request = new Request.Builder()
                .url("http://api.obutts.ru/butts/0/1/random")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response);
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return "http://media.obutts.ru/" + new JSONObject(Objects.requireNonNull(responseBody).string()
                    .replace("[", "")
                    .replace("]", ""))
                    .get("preview")
                    .toString();
        }
    }

}
