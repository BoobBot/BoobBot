package bot.boobbot.commands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.*
import java.awt.Color
import java.net.URLEncoder

@CommandProperties(description = "Super freaky porn <:p_:475801484282429450>", nsfw = true, guildOnly = true, donorOnly = true, category = CommandProperties.category.GENERAL)
class Magik : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbmagik <type>\nTypes: boobs, ass, dick"))
            }
        }

        val url: String

        when (ctx.args[0]) {
            "boobs" -> {
                val oboobs = BoobBot.requestUtil
                        .get("http://api.oboobs.ru/boobs/0/1/random").await()?.jsonArray()
                        ?: return ctx.send("rip some error, press f")

                url = "http://media.oboobs.ru/" + oboobs.getJSONObject(0).getString("preview")
            }
            "ass" -> {
                val obutts = BoobBot.requestUtil
                        .get("http://api.obutts.ru/butts/0/1/random").await()?.jsonArray()
                        ?: return ctx.send("rip some error, press f")

                url = "http://media.obutts.ru/" + obutts.getJSONObject(0).getString("preview")
            }
            "dick" -> {
                val headers = createHeaders(
                        Pair("Key", Constants.BB_API_KEY)
                )

                val bb = BoobBot.requestUtil
                        .get("https://boob.bot/api/v2/img/penis", headers).await()?.json()
                        ?: return ctx.send("rip some error, press f")

                url = bb.getString("url")
            }
            else -> {
                return ctx.embed {
                    setColor(Color.red)
                    setDescription(Formats.error("What?\nTypes: boobs, ass, dick"))
                }
            }
        }

        val res = BoobBot.requestUtil
                .get(
                        "https://dankmemer.services/api/magik?avatar1=${URLEncoder.encode(url, Charsets.UTF_8.name())}",
                        createHeaders(Pair("Authorization", Constants.MEMER_IMGEN_KEY))
                )
                .await()
                ?: return ctx.send("rip some error, press f")

        val body = res.body() ?: return ctx.send("rip some error, press f")

        ctx.channel.sendFile(body.byteStream(), "magik.png").queue()
    }

}