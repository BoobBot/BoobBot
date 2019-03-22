package bot.boobbot.misc

import io.github.cdimascio.dotenv.Dotenv
import java.util.concurrent.CompletionStage

fun Dotenv.get(key: String, default: String): String = get(key) ?: default

fun <T> CompletionStage<T>.thenException(handler: ((Throwable) -> Unit)?): CompletionStage<T> {
    exceptionally {
        if (handler != null) {
            handler(it)
        }
        return@exceptionally null
    }

    return this
}