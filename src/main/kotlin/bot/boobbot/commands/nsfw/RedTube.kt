package bot.boobbot.commands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import java.awt.Color
import java.time.Instant

@CommandProperties(
    description = "RedTube video search.",
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
            return ctx.send {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbrt <tag> or random\n"))
            }
        }

        val query = if (ctx.args[0].lowercase() != "random") ctx.args[0].lowercase() else Formats.tag.random()

        val rt = BoobBot.requestUtil.get(urlFor(query)).await()?.json()?.takeIf { it.has("videos") }
            ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

        val video = rt.getJSONArray("videos").getJSONObject(0).getJSONObject("video")
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.send {
            setAuthor("RedTube video search", video.getString("embed_url"), "https://cdn.discordapp.com/attachments/440667148315262978/490353839577497623/rt.png")
            setTitle(video.getString("title"), video.getString("url"))
            setDescription("RedTube video search")
            setColor(Colors.getEffectiveColor(ctx.message))
            setImage(video.getString("thumb"))
            addField(
                "Video stats",
                "Views: ${video.get("views")}\n" +
                        "Rating: ${video.get("rating")}\n" +
                        "Ratings: ${video.get("ratings")}\n" +
                        "Duration: ${video.get("duration")}\n" +
                        "Date published: ${video.get("publish_date")}\n" +
                        "Url: ${video.get("url")}",
                false
            )
            setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }
}
