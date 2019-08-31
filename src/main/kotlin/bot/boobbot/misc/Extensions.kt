package bot.boobbot.misc

import club.minnced.discord.webhook.send.WebhookEmbed
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.entities.MessageEmbed
import java.net.URI

fun Dotenv.get(key: String, default: String): String = get(key) ?: default

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
        if (this.author != null) WebhookEmbed.EmbedAuthor(this.author!!.name ?: "", this.author!!.iconUrl, this.author!!.url) else null,
        this.fields.map { WebhookEmbed.EmbedField(it.isInline, it.name ?: "", it.value ?: "") }
    )
}