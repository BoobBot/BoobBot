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


abstract class RtCommand : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbrt <tag> or random\n"))
            }
        }
        try {
            val rt = BoobBot.requestUtil.get(
                "https://api.redtube.com/?data=redtube.Videos.searchVideos&output=json&search=" +
                        (if (ctx.args[0].toLowerCase() != "random") ctx.args[0].toLowerCase() else Formats.tag[Random().nextInt(
                            Formats.tag.size
                        )]) +
                        "&thumbsize=big&ordering=mostviewed&page=1",
                useProxy = false
            ).await()?.json()
                ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

            val video = rt.getJSONArray("videos").getJSONObject(0).getJSONObject("video")
            //BoobBot.log.info(video.toString(3))
            ctx.embed {
                setAuthor(
                    "RedTube video search",
                    video.getString("embed_url"),
                    "https://cdn.discordapp.com/attachments/440667148315262978/490353839577497623/rt.png"
                )
                setTitle(video.getString("title"), video.getString("url"))
                setDescription("RedTube video search")
                setColor(Colors.getEffectiveColor(ctx.message))
                setImage(video.getString("thumb"))
                addField(
                    "Video stats",
                    "Views: ${video.get("views")}\n" +
                            "Rating: ${video.getString("rating")}\n" +
                            "Ratings: ${video.getString("ratings")}\n" +
                            "Duration: ${video.getString("duration")}\n" +
                            "Date published: ${video.getString("publish_date")}\n" +
                            "Url: ${video.getString("url")}",
                    false
                )
                setFooter("Requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
                setTimestamp(now())
                build()
            }
        } catch (Ex: Exception) {
            BoobBot.log.error(Ex.toString())
            ctx.send("\uD83D\uDEAB oh? something broken af")
        }

    }
}
