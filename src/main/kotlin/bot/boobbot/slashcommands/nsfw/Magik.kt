package bot.boobbot.slashcommands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.FileUpload
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.awt.Color

@CommandProperties(
    description = "Super freaky porn.",
    nsfw = true,
    guildOnly = true,
    donorOnly = true,
    category = Category.GENERAL
)
class Magik : AsyncSlashCommand {

    private val baseUrl = "https://memes.subspace.gg/api/magik".toHttpUrlOrNull()!!
    private val categories = mapOf(
        "dick" to "penis",
        "boobs" to "boobs",
        "ass" to "ass"
    )

    override suspend fun executeAsync(event: SlashCommandInteractionEvent) {
        val category = categories[event.getOption("category")!!.asString]
            ?: return event.replyEmbeds(
            EmbedBuilder().apply {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbmagik <type>\nTypes: boobs, ass, dick\n"))
            }.build()).queue()



        val imageUrl = getImage(category)
            ?: return event.reply("API didn't respond with an image URL, rip").queue()

        val image = BoobBot.requestUtil
            .get(
                baseUrl.newBuilder().addQueryParameter("avatar1", imageUrl).build().toString(),
                headersOf("Authorization", BoobBot.config.MEMER_IMGEN_KEY)
            )
            .await()
            ?.body
            ?: return event.reply("API didn't respond with an image, rip").queue()
        event.replyFiles(FileUpload.fromData(image.byteStream(), "magik.png")).queue()
    }

    private suspend fun getImage(category: String): String? {
        return BoobBot.requestUtil
            .get("https://boob.bot/api/v2/img/$category", headersOf("Key", BoobBot.config.BB_API_KEY))
            .await()
            ?.json()
            ?.getString("url")
    }
}
