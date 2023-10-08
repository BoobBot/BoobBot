package bot.boobbot.commands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.Choice
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import net.dv8tion.jda.api.utils.FileUpload
import okhttp3.Headers.Companion.headersOf
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.awt.Color

@CommandProperties(
    description = "Super freaky porn.",
    nsfw = true,
    guildOnly = true,
    donorOnly = true,
    category = Category.GENERAL
)
@Option(name = "category", description = "The image category to use.", choices = [Choice("Dick", "dick"), Choice("Boobs", "boobs"), Choice("Ass", "ass")])
class Magik : AsyncCommand {
    companion object {
        private val baseUrl = "https://memes.boob.bot/api/magik".toHttpUrlOrNull()!!
        private val categories = mapOf(
            "dick" to "penis",
            "boobs" to "boobs",
            "ass" to "ass"
        )
    }

    override suspend fun executeAsync(ctx: Context) {
        val category = ctx.options.getByNameOrNext("category", Resolver.STRING)?.let(categories::get)
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\n/magik <type>\nTypes: boobs, ass, dick"))
            }

        val imageUrl = getImage(category)
            ?: return ctx.reply("API didn't respond with an image URL, rip")

        val image = BoobBot.requestUtil
            .get(baseUrl.newBuilder().addQueryParameter("avatar1", imageUrl).build().toString(), headersOf("Authorization", BoobBot.config.MEMER_IMGEN_KEY))
            .await()
            ?.body
            ?: return ctx.reply("API didn't respond with an image, rip")

        ctx.reply(FileUpload.fromData(image.byteStream(), "magik.png"))
    }

    private suspend fun getImage(category: String): String? {
        return BoobBot.requestUtil
            .get("https://boob.bot/api/v2/img/$category", headersOf("Key", BoobBot.config.BB_API_KEY))
            .await()
            ?.json()
            ?.getString("url")
    }
}
