package bot.boobbot.misc

import bot.boobbot.BoobBot
import kotlinx.coroutines.experimental.future.await
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CompletableFuture

class RequestUtil {
    private val userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"
    private val httpClient = OkHttpClient()

    inner class PendingRequest(private val request: Request) {

        fun queue(success: (Response?) -> Unit) {
            httpClient.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    BoobBot.log.error("An error occurred during a HTTP request to ${call.request().url()}", e)
                    success(null) // This could/should be `failure` but this method allows us to do
                    // `.queue { it?.json() ?: return }` which looks cleaner overall. Exception handling is also done above.
                }

                override fun onResponse(call: Call, response: Response) {
                    success(response)
                }

            })
        }

        suspend fun await(): Response? {
            val future = CompletableFuture<Response?>()

            queue {
                future.complete(it)
            }

            return future.await()
        }

    }

    public fun get(url: String, headers: Headers = Headers.of()): PendingRequest {
        return makeRequest("GET", url, null, headers)
    }

    public fun post(url: String, body: RequestBody, headers: Headers): PendingRequest {
        return makeRequest("POST", url, body, headers)
    }

    public fun makeRequest(method: String, url: String, body: RequestBody? = null, headers: Headers): PendingRequest {
        val request = Request.Builder()
                .method(method.toUpperCase(), body)
                .header("User-Agent", userAgent)
                .headers(headers)
                .url(url)

        return PendingRequest(request.build())
    }
}

fun Response.json(): JSONObject? {
    val body = body()

    body().use {
        return if (isSuccessful && body != null) {
            JSONObject(body()!!.string())
        } else {
            null
        }
    }
}

public fun createHeaders(vararg kv: Pair<String, String>): Headers {
    val builder = Headers.Builder()

    for (header in kv) {
        builder.add(header.first, header.second)
    }

    return builder.build()
}
