package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.json
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.awt.Color
import java.time.Instant.now
import java.util.*


abstract class PhCommand : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbrt <tag> or random\n"))
            }
        }
        val rt = BoobBot.requestUtil.get(
                "https://www.pornhub.com/webmasters/search?search=${if (ctx.args[0].toLowerCase() != "random") ctx.args[0].toLowerCase() else Formats.tag[Random().nextInt(Formats.tag.size)]}&output=json${(0..99999).first}",
                useProxy = true).await()?.json()
                ?: return ctx.send("\uD83D\uDEAB oh? something broken af")
        val video = rt.getJSONArray("videos").getJSONObject(0)
        ctx.embed {
            setAuthor("PornHub video search",
                    video.getString("url"),
                    "https://data.apkhere.com/b2/com.app.pornhub/4.1.1/icon.png!s")
                    .setTitle(video.getString("title"), video.getString("url"))
                    .setDescription("RedTube video search")
                    .setColor(Colors.getEffectiveColor(ctx.message))
                    .setImage(video.getString("thumb"))
                    .addField("Video stats",
                            "Views: ${video.getString("views")}\n" +
                                    "Rating: ${video.get("rating")}\n" +
                                    "Ratings: ${video.get("ratings")}\n" +
                                    "Duration: ${video.getString("duration")}\n" +
                                    "Date published: ${video.getString("publish_date")}\n" +
                                    "Url: ${video.getString("url")}",
                            false)

                    .setFooter("Requested by ${ctx.author.name}", ctx.author.avatarUrl)
                    .setTimestamp(now())
                    .build()
        }
    }
}