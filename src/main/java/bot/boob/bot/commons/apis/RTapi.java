package bot.boob.bot.commons.apis;

import bot.boob.bot.commons.Misc;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class RTapi {
    private static final OkHttpClient client = new OkHttpClient();

    public static JSONObject makeReqAndGetRes(String tag) throws Exception {
        OkHttpClient rtClient = client.newBuilder()
                .proxy(Misc.getProxy())
                .build();
        Request req = new Request.Builder().url("https://api.redtube.com/?data=redtube.Videos.searchVideos&output=json&search=" + tag + "&thumbsize=big&ordering=mostviewed&page=1")
                .build();
        Response res = rtClient.newCall(req).execute();
        try (ResponseBody responseBody = res.body()) {
            if (!res.isSuccessful()) throw new IOException("shit, req failed with " + res + " wsi down again?");
            return new JSONObject(Objects.requireNonNull(responseBody).string());
        }
    }

}
