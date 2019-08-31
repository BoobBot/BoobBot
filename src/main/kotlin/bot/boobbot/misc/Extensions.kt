package bot.boobbot.misc

import io.github.cdimascio.dotenv.Dotenv
import java.net.URL

fun Dotenv.get(key: String, default: String): String = get(key) ?: default

fun <T> Array<T>.separate(): Pair<T, List<T>> = Pair(first(), drop(1))
fun <T> List<T>.separate(): Pair<T, List<T>> = Pair(first(), drop(1))

fun String.toUrlOrNull(): URL? {
    return try {
        URL(this)
    } catch (e: Exception) {
        return null
    }
}
