package bot.boobbot.misc

import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.core.entities.Game
import java.lang.Exception
import java.net.URL
import java.util.concurrent.CompletionStage

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
