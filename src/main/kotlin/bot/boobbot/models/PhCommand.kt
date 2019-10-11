package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.json
import java.awt.Color
import java.time.Instant.now
import java.util.*


abstract class PhCommand : AsyncCommand {
    private fun urlFor(query: String): String {
        return "https://www.pornhub.com/webmasters/search?search=$query&output=json${(0..99999).random()}"
    }

    override suspend fun executeAsync(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbrt <tag> or random\n"))
            }
        }

        val query = if (ctx.args[0].toLowerCase() != "random") ctx.args[0].toLowerCase() else Formats.tag.random()

        val rt = BoobBot.requestUtil.get(urlFor(query)).await()?.json()
            ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

        val video = rt.getJSONArray("videos").getJSONObject(0)

        ctx.embed {
            setAuthor(
                "PornHub video search",
                video.getString("url"),
                "https://data.apkhere.com/b2/com.app.pornhub/4.1.1/icon.png!s"
            )
            setTitle(video.getString("title"), video.getString("url"))
            setDescription("PornTube video search")
            setColor(Colors.getEffectiveColor(ctx.message))
            setImage(video.getString("thumb"))
            addField(
                "Video stats",
                "Views: ${video.get("views")}\n" +
                        "Rating: ${video.get("rating")}\n" +
                        "Ratings: ${video.get("ratings")}\n" +
                        "Duration: ${video.getString("duration")}\n" +
                        "Date published: ${video.getString("publish_date")}\n" +
                        "Url: ${video.getString("url")}",
                false
            )
            setFooter("Requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
            setTimestamp(now())
            build()
        }
    }
}
