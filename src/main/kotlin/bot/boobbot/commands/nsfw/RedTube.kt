package bot.boobbot.commands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.json
import java.awt.Color
import java.time.Instant

@CommandProperties(
    description = "RedTube video search <:p_:475801484282429450>",
    guildOnly = true,
    aliases = ["rt"],
    nsfw = true,
    category = Category.VIDEOSEARCHING,
    donorOnly = true
)
class RedTube : AsyncCommand {
    private fun urlFor(query: String): String {
        return "https://api.redtube.com/?data=redtube.Videos.searchVideos&output=json&thumbsize=big&ordering=mostviewed&page=1&search=$query"
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

        val video = rt.getJSONArray("videos").getJSONObject(0).getJSONObject("video")
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
            setTimestamp(Instant.now())
            build()
        }
    }
}
