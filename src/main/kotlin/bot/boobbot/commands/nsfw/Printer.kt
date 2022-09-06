package bot.boobbot.commands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers.Companion.headersOf
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.awt.Color

@CommandProperties(
    description = "Super freaky text porn.",
    nsfw = true,
    guildOnly = true,
    category = Category.GENERAL
)
class Printer : AsyncCommand {

    private val baseUrl = "http://127.0.0.1:7080/printer".toHttpUrlOrNull()!!
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

    override suspend fun executeAsync(ctx: MessageContext) {
        val category = categories[ctx.args.firstOrNull()]
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbprinter <type>\nTypes: $typeString"))
            }

        val imageUrl = getImage(category)
            ?: return ctx.reply("API didn't respond with an image URL, rip")

        val image = BoobBot.requestUtil
            .get(baseUrl.newBuilder().addQueryParameter("url", imageUrl).build().toString(), headersOf())
            .await()
            ?.body
            ?.string()
            ?: return ctx.reply("API didn't respond, rip")

        if (image.length > 2000) {
            return ctx.reply("rip, too big for discord")
        }

        ctx.reply("```\n$image```")
    }

    private suspend fun getImage(category: String): String? {
        return BoobBot.requestUtil
            .get("https://boob.bot/api/v2/img/$category", headersOf("Key", BoobBot.config.BB_API_KEY))
            .await()
            ?.json()
            ?.getString("url")
    }
}
