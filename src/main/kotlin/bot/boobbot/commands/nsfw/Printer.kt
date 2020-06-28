package bot.boobbot.commands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers
import okhttp3.HttpUrl
import java.awt.Color

@CommandProperties(
    description = "Super freaky text porn <:p_:475801484282429450>",
    nsfw = true,
    guildOnly = true,
    category = Category.GENERAL
)
class Printer : AsyncCommand {

    private val baseUrl = HttpUrl.parse("http://127.0.0.1:7080/printer")!!
    private val categories = mapOf(
        "dick" to "penis",
        "boobs" to "boobs",
        "ass" to "ass"
    )

    override suspend fun executeAsync(ctx: Context) {
        val category = categories[ctx.args.firstOrNull()]
            ?: return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbprinter <type>\nTypes: boobs, ass, dick"))
            }

        val imageUrl = getImage(category)
            ?: return ctx.send("API didn't respond with an image URL, rip")

        val image = BoobBot.requestUtil
            .get(
                baseUrl.newBuilder().addQueryParameter("url", imageUrl).build().toString(),
                Headers.of()
            )
            .await()
            ?.body()
            ?.string()
            ?: return ctx.send("API didn't respond, rip")

        if (image.length > 2000) {
            return ctx.send("rip, too big for discord")
        }

        ctx.send("```\n$image```")
    }

    private suspend fun getImage(category: String): String? {
        return BoobBot.requestUtil
            .get(
                "https://boob.bot/api/v2/img/$category",
                Headers.of("Key", BoobBot.config.BB_API_KEY)
            )
            .await()
            ?.json()
            ?.getString("url")
    }
}
