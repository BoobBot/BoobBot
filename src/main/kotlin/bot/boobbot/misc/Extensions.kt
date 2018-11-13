package bot.boobbot.misc

import kotlinx.coroutines.future.await
import net.dv8tion.jda.core.requests.RestAction

suspend fun <T> RestAction<T>.await(): T? {
    return try {
        submit().await()
    } catch (e: Exception) {
        null
    }
}