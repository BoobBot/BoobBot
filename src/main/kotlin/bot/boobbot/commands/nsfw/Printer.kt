package bot.boobbot.commands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.flight.*
import bot.boobbot.misc.Formats
import bot.boobbot.misc.createHeaders
import bot.boobbot.misc.json
import bot.boobbot.misc.jsonArray
import java.awt.Color
import java.net.URLEncoder

@CommandProperties(
    description = "Printed porn",
    nsfw = true,
    guildOnly = true,
    donorOnly = false,
    category = Category.GENERAL
)
class Printer : Command {

    private val headers = createHeaders(Pair("Key", BoobBot.config.bbApiKey))

    override fun execute(ctx: Context) {
        return ctx.embed {
            setColor(Color.red)
            setDescription(Formats.error("Missing Args\nbbprinter <type>\nTypes: ${subcommands.keys.joinToString(", ")}"))
        }
    }

    @SubCommand(async = true)
    suspend fun boobs(ctx: Context) {
        val res = BoobBot.requestUtil.get("http://api.oboobs.ru/boobs/0/1/random")
            .await()
            ?.jsonArray() ?: return ctx.send("Some API error, shit")

        val preview = res.getJSONObject(0).getString("preview")
        print(ctx, "http://media.oboobs.ru/$preview")
    }

    @SubCommand(async = true)
    suspend fun ass(ctx: Context) {
        val res = BoobBot.requestUtil.get("http://api.obutts.ru/boobs/0/1/random")
            .await()
            ?.jsonArray() ?: return ctx.send("Some API error, shit")

        val preview = res.getJSONObject(0).getString("preview")
        print(ctx, "http://media.obutts.ru/$preview")
    }

    @SubCommand(async = true)
    suspend fun dicks(ctx: Context) {
        val bb = BoobBot.requestUtil
            .get("https://boob.bot/api/v2/img/penis", headers)
            .await()
            ?.json() ?: return ctx.send("Some API error, shit")

        print(ctx, bb.getString("url"))
    }

    suspend fun print(ctx: Context, url: String) {
        val res = BoobBot.requestUtil
            .get("https://api.qoilo.com/printer?url=${URLEncoder.encode(url, Charsets.UTF_8.name())}")
            .await()
            ?: return ctx.send("rip some error, press f")

        if (!res.isSuccessful) {
            return ctx.send("API error, shit")
        }

        val str = res.body()?.string() ?: return ctx.send("rip some error, press f")

        if (str.length > 2000) {
            return ctx.send("Can't send result; too big for Discord")
        }

        ctx.send("```$str```")
    }

}