package bot.boob.bot.commons.apis;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class bbapi {
    private static final OkHttpClient client = new OkHttpClient();

    public static String getThigh() throws Exception {
        Request request = new Request.Builder()
                .url("https://boob.bot/api/v2/img/ThighBot")
                .header("key", "JDaK5IEeziTtABsWrkJWtv9yjW4Gu18B")
                .build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String makeReqAndGetResAsString(String path, String jsonKey) throws Exception {
        Request req = new Request.Builder().url("https://boob.bot/api/v2/img/" + path)
                .header("key", "JDaK5IEeziTtABsWrkJWtv9yjW4Gu18B")
                .build();
        Response res = client.newCall(req).execute();
        try (ResponseBody responseBody = res.body()) {
            if (!res.isSuccessful()) throw new IOException("shit, req failed with " + res + " wsi down again?");
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get(jsonKey).toString();
        }
    }
}
