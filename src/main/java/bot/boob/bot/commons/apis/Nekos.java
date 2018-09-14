package bot.boob.bot.commons.apis;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class Nekos {
    private static final OkHttpClient client = new OkHttpClient();

    public static String makeReqAndGetResAsString(String path, String jsonKey) throws Exception {
        Request req = new Request.Builder().url("\"https://nekos.life/api/v2/img/" + path).build();
        Response res = client.newCall(req).execute();
        try (ResponseBody responseBody = res.body()) {
            if(!res.isSuccessful()) throw new IOException("shit, req failed with "+res+" wsi down again?");
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get(jsonKey).toString();
        }
    }

    public static String getMeme() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/gecg").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }


    public static String getBall() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/8ball").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String res = responseBody.string();
            return res;
        }
    }

    public static String getAvatar() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/avatar").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getNsfwAvatar() throws Exception {
        Request request =
                new Request.Builder().url("https://nekos.life/api/v2/img/nsfw_avatar").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getCat() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/cat").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("cat").toString();
        }
    }

    public static String getSlap() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/slap").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getPoke() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/poke").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getAnal() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/anal").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getNeko() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/neko").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getLewd() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/lewd").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getFox() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/fox_girl").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getKuni() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/kuni").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getHug() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/hug").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getCuddle() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/cuddle").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getPat() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/pat").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getKiss() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/img/kiss").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("url").toString();
        }
    }

    public static String getWhy() throws Exception {
        Request request = new Request.Builder().url("https://nekos.life/api/v2/why").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("why").toString();
        }
    }

    public static String getChat(boolean owo, String input) throws Exception {
        String url = "https://nekos.life/api/v2/chat?text=" + input;
        if (owo) {
            url = url + "&owo=true";
        }
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string())
                    .get("response")
                    .toString();
        }
    }
}
