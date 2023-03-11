package bot.boobbot.utils

import club.minnced.discord.webhook.send.WebhookEmbed
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import okhttp3.Response
import org.json.JSONObject
import java.net.URI
import java.util.concurrent.CompletableFuture

fun Dotenv.get(key: String, default: String): String = get(key) ?: default

fun <T> List<T>.discard(amount: Int) = if (amount > 0) this.drop(amount) else this

fun <T> List<T>.separate(): Pair<T, List<T>> = Pair(first(), drop(1))

fun String.toUriOrNull(): URI? {
    return try {
        URI(this)
    } catch (e: Exception) {
        return null
    }
}

fun MessageEmbed.toWebhookEmbed(): WebhookEmbed {
    return WebhookEmbed(
        this.timestamp,
        this.colorRaw,
        this.description,
        this.thumbnail?.url,
        this.image?.url,
        if (this.footer != null) WebhookEmbed.EmbedFooter(this.footer!!.text ?: "", this.footer!!.iconUrl) else null,
        WebhookEmbed.EmbedTitle(this.title ?: "", this.url),
        if (this.author != null) WebhookEmbed.EmbedAuthor(
            this.author!!.name ?: "",
            this.author!!.iconUrl,
            this.author!!.url
        ) else null,
        this.fields.map { WebhookEmbed.EmbedField(it.isInline, it.name ?: "", it.value ?: "") }
    )
}

fun MessageEmbed.asMessage() = MessageCreateBuilder().setEmbeds(this).build()

fun Response.json(): JSONObject? {
    return body?.takeIf { isSuccessful }?.use { JSONObject(it.string()) }
}

suspend fun <T> CompletableFuture<T>.awaitSuppressed(): T? {
    return try {
        this.await()
    } catch (e: Exception) {
        null
    }
}

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

fun <T> Collection<T>.ifEmpty(trueValue: String, falseValue: Collection<T>.() -> String): String {
    if (this.isEmpty()) {
        return trueValue
    }

    return falseValue(this)
}

fun <K, V> Map<K, V>.joinToString(separator: CharSequence) = "{\n${this.entries.joinToString(separator) { "${it.key}=${it.value}" }}\n}"