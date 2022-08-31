package bot.boobbot.utils

import bot.boobbot.BoobBot
import kotlinx.coroutines.future.await
import okhttp3.*
import okhttp3.Headers.Companion.headersOf
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class RequestUtil {
    private val userAgent =
        "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(LoggingInterceptor())
        .connectionPool(ConnectionPool(200, 5L, TimeUnit.MINUTES))
        .retryOnConnectionFailure(true)
        .protocols(listOf(Protocol.HTTP_1_1))
        .build()

    private val proxiedHttpClient: OkHttpClient
        get() = httpClient.newBuilder().proxy(Utils.getProxy()).build()

    inner class PendingRequest(private val request: Request, private val useProxy: Boolean = false) {
        fun submit(): CompletableFuture<Response> {
            val fut = CompletableFuture<Response>()
            val client = if (useProxy) proxiedHttpClient else httpClient

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    BoobBot.log.error("An error occurred during a HTTP request to ${call.request().url}", e)
                    fut.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    fut.complete(response)
                }
            })

            return fut
        }

        fun queue(success: (Response?) -> Unit) {
            submit().thenAccept(success).thenException { success(null) }
        }

        suspend fun await(): Response? = submit().await()
    }

    fun get(url: String, headers: Headers = headersOf(), useProxy: Boolean = false): PendingRequest {
        return makeRequest(useProxy, "GET", url, null, headers)
    }

    fun makeRequest(
        useProxy: Boolean = false,
        method: String,
        url: String,
        body: RequestBody? = null,
        headers: Headers
    ): PendingRequest {
        val request = Request.Builder()
            .method(method.uppercase(), body)
            .header("User-Agent", userAgent)
            .headers(headers)
            .url(url)

        return makeRequest(request.build(), useProxy)
    }

    fun makeRequest(req: Request, useProxy: Boolean = false): PendingRequest {
        return PendingRequest(req, useProxy)
    }

    companion object {
        internal class LoggingInterceptor : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                if (BoobBot.isDebug) {
                    //BoobBot.log.info(request.toString())
                    //BoobBot.log.info(response.toString())
                }
                return chain.proceed(request)
            }
        }
    }
}
