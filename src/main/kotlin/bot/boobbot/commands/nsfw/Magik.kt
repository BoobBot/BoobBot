package bot.boobbot.commands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import bot.boobbot.misc.createHeaders
import bot.boobbot.misc.json
import bot.boobbot.misc.jsonArray
import okhttp3.Headers
import okhttp3.HttpUrl
import java.awt.Color
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@CommandProperties(
    description = "Super freaky porn <:p_:475801484282429450>",
    nsfw = true,
    guildOnly = true,
    donorOnly = true,
    category = Category.GENERAL
)
class Magik : AsyncCommand {

    private val baseUrl = HttpUrl.parse("https://dankmemer.services/api/magik")!!
    private val categories = mapOf(
        "dick" to "penis",
        "boobs" to "boobs",
        "ass" to "ass"
    )

    override suspend fun executeAsync(ctx: Context) {
        val category = categories[ctx.args.firstOrNull()]
            ?: return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbmagik <type>\nTypes: boobs, ass, dick"))
            }

        val imageUrl = getImage(category)
            ?: return ctx.send("API didn't respond with an image URL, rip")

        val image = BoobBot.requestUtil
            .get(
                baseUrl.newBuilder().addQueryParameter("avatar1", imageUrl).build().toString(),
                Headers.of("Authorization", BoobBot.config.memerImgenKey)
            )
            .await()
            ?.body()
            ?: return ctx.send("API didn't respond with an image, rip")

        ctx.channel.sendFile(image.byteStream(), "magik.png").queue()
    }

    /**
     * @returns URL to the image.
     */
    private suspend fun getImage(category: String): String? {
        return BoobBot.requestUtil
            .get(
                "https://boob.bot/api/v2/img/$category",
                Headers.of("Key", BoobBot.config.bbApiKey)
            )
            .await()
            ?.json()
            ?.getString("url")
    }

}