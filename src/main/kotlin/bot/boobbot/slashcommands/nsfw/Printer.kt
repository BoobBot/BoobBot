package bot.boobbot.slashcommands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.awt.Color

@CommandProperties(
    description = "Super freaky text porn.",
    nsfw = true,
    guildOnly = true,
    category = Category.GENERAL
)
class Printer : AsyncSlashCommand {

    private val baseUrl = "https://printer.boob.bot/printer".toHttpUrlOrNull()!!
    private val categories = mapOf(
        "dick" to "penis",
        "boobs" to "boobs",
        "ass" to "ass",
        "black" to "black",
        "tentacle" to "tentacle",
        "pawg" to "pawg",
        "hentai" to "hentai",
        "easter" to "easter"
    )
    val typeString = categories.keys.joinToString("`, `", prefix = "`", postfix = "`")

    override suspend fun executeAsync(event: SlashCommandInteractionEvent) {
        val category = categories[event.getOption("category")!!.asString]
            ?: return event.replyEmbeds(
                EmbedBuilder().apply {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbprinter <type>\nTypes: $typeString"))
            }.build()).queue()

        val imageUrl = getImage(category)
            ?: return event.reply("API didn't respond with an image URL, rip").queue()

        val image = BoobBot.requestUtil
            .get(
                baseUrl.newBuilder().addQueryParameter("url", imageUrl).build().toString(),
                headersOf()
            )
            .await()
            ?.body
            ?.string()
            ?: return event.reply("API didn't respond, rip").queue()

        if (image.length > 2000) {
            return event.reply("rip, too big for discord").queue()
        }

        event.reply("```\n$image```").queue()
    }

    private suspend fun getImage(category: String): String? {
        return BoobBot.requestUtil
            .get(
                "https://boob.bot/api/v2/img/$category",
                headersOf("Key", BoobBot.config.BB_API_KEY)
            )
            .await()
            ?.json()
            ?.getString("url")
    }
}
