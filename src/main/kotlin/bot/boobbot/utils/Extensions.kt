package bot.boobbot.utils

import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.requests.RestAction
import okhttp3.Response
import okio.use
import org.json.JSONObject
import java.net.URI
import java.sql.Timestamp
import java.time.Instant
import java.util.concurrent.CompletableFuture

fun <T> List<T>.discard(amount: Int) = if (amount > 0) this.drop(amount) else this

fun <T> List<T>.separate(): Pair<T, List<T>> = Pair(first(), drop(1))

fun String.toUriOrNull() = this.runCatching(::URI).getOrNull()

inline fun <T> Boolean.ifTrue(body: () -> T?): T? = if (this) body() else null

fun Response.json() = body.use { it.takeIf { isSuccessful }?.string()?.let(::JSONObject) }

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long) = sumOf(selector)

suspend fun <T> CompletableFuture<T>.awaitSuppressed(): T? = this.runCatching { await() }.getOrNull()

fun <T> CompletableFuture<T>.thenException(block: (Throwable) -> Unit) {
    this.exceptionally {
        block(it)
        return@exceptionally null
    }
}

fun <T, O> RestAction<T>.intersect(other: List<O>, apply: (T, Int, O) -> RestAction<T>): RestAction<T> {
    var last = this
    for ((i, e) in other.withIndex()) {
        last = last.flatMap { apply(it, i, e) }
    }
    return last
}

fun <T> Collection<T>.ifEmpty(trueValue: String, falseValue: Collection<T>.() -> String) = if (isEmpty()) trueValue else falseValue(this)

fun <K, V> Map<K, V>.joinToString(separator: CharSequence) = "{\n${this.entries.joinToString(separator) { "${it.key}=${it.value}" }}\n}"

fun Instant?.toTimestamp(): Timestamp {
    return this?.let(Timestamp::from) ?: Timestamp(0)
}
