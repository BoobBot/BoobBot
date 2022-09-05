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
    description = "PornHub video search.",
    donorOnly = true,
    guildOnly = true,
    aliases = ["ph"],
    nsfw = true,
    category = Category.VIDEOSEARCHING
)
class PornHub : AsyncCommand {
    private fun urlFor(query: String): String {
        return "https://www.pornhub.com/webmasters/search?search=$query&output=json${(0..99999).random()}"
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

        val video = rt.getJSONArray("videos").getJSONObject(0)
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.send {
            setAuthor("PornHub video search", video.getString("url"), "https://data.apkhere.com/b2/com.app.pornhub/4.1.1/icon.png!s")
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
            setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }
}
