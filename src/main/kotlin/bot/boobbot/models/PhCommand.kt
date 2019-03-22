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

    override suspend fun executeAsync(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.embed {
                color(Color.red)
                description(Formats.error("Missing Args\nbbrt <tag> or random\n"))
            }
        }
        val rt = BoobBot.requestUtil.get(
            "https://www.pornhub.com/webmasters/search?search=${if (ctx.args[0].toLowerCase() != "random") ctx.args[0].toLowerCase() else Formats.tag[Random().nextInt(
                Formats.tag.size
            )]}&output=json${(0..99999).first}",
            useProxy = true
        ).await()?.json()
            ?: return ctx.send("\uD83D\uDEAB oh? something broken af")
        val video = rt.getJSONArray("videos").getJSONObject(0)
        ctx.embed {
            author(
                "PornHub video search",
                video.getString("url"),
                "https://data.apkhere.com/b2/com.app.pornhub/4.1.1/icon.png!s"
            )
            title(video.getString("title"))
            url(video.getString("url"))
            description("PornTube video search")
            color(Colors.getEffectiveColor(ctx.message))
            image(video.getString("thumb"))
            field(
                "Video stats",
                "Views: ${video.get("views")}\n" +
                        "Rating: ${video.get("rating")}\n" +
                        "Ratings: ${video.get("ratings")}\n" +
                        "Duration: ${video.getString("duration")}\n" +
                        "Date published: ${video.getString("publish_date")}\n" +
                        "Url: ${video.getString("url")}",
                false
            )
            footer("Requested by ${ctx.author.username()}", ctx.author.effectiveAvatarUrl())
            timestamp(now())
            build()
        }
    }
}