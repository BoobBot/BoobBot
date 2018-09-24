package bot.boobbot.misc

import bot.boobbot.BoobBot
import kotlinx.coroutines.experimental.future.await
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CompletableFuture


internal class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        BoobBot.log.info("REQUEST INFO", request.toString())
        return chain.proceed(request)
    }
}
class RequestUtil {
    private val userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"
    private val httpClient = OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()

    inner class PendingRequest(private val request: Request, private var useProxy: Boolean = false) {


        fun queue(success: (Response?) -> Unit) {
            var client = httpClient
            if (useProxy) {
                client = client.newBuilder().proxy(Utils.getProxy()).build() // this is needed for ph/rt reqs due to rape-limits
            }
            client.newCall(request).enqueue(object : Callback {

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

        fun block(): Response? {
            return try {
                httpClient.newCall(request).execute()
            } catch (e: IOException) {
                BoobBot.log.error("An error occurred during a HTTP request to ${request.url()}", e)
                null
            }
        }

    }

    fun get(url: String, headers: Headers = Headers.of(), useProxy: Boolean = false): PendingRequest {
        return makeRequest(useProxy, "GET", url, null, headers)
    }

    fun post(url: String, body: RequestBody, headers: Headers, useProxy: Boolean = false): PendingRequest {
        return makeRequest(useProxy, "POST", url, body, headers)
    }

    fun makeRequest(useProxy: Boolean = false, method: String, url: String, body: RequestBody? = null, headers: Headers): PendingRequest {
        val request = Request.Builder()
                .method(method.toUpperCase(), body)
                .header("User-Agent", userAgent)
                .headers(headers)
                .url(url)

        return PendingRequest(request.build(), useProxy)
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

fun Response.jsonArray(): JSONArray? {
    val body = body()

    body().use {
        return if (isSuccessful && body != null) {
            JSONArray(body()!!.string())
        } else {
            null
        }
    }
}

fun createHeaders(vararg kv: Pair<String, String>): Headers {
    val builder = Headers.Builder()

    for (header in kv) {
        builder.add(header.first, header.second)
    }

    return builder.build()
}
