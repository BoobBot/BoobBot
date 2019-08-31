package bot.boobbot.misc

import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.core.entities.Game
import java.util.concurrent.CompletionStage

fun Dotenv.get(key: String, default: String): String = get(key) ?: default

fun <T> Array<T>.separate(): Pair<T, List<T>> = Pair(first(), drop(1))
fun <T> List<T>.separate(): Pair<T, List<T>> = Pair(first(), drop(1))
