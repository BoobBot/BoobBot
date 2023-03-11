package bot.boobbot.commands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.Option
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
@Option(name = "query", description = "The search query.")
class PornHub : AsyncCommand {
    private fun urlFor(query: String): String {
        return "https://www.pornhub.com/webmasters/search?search=$query&output=json${(0..99999).random()}"
    }

    override suspend fun executeAsync(ctx: Context) {
        val userQuery = ctx.options.getOptionStringOrGather("query")
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\n/pornhub <tag> or random\n"))
            }

        val query = if (userQuery.lowercase() != "random") userQuery.lowercase() else Formats.tag.random()

        val rt = BoobBot.requestUtil.get(urlFor(query)).await()?.json()?.takeIf { it.has("videos") }
            ?: return ctx.reply("\uD83D\uDEAB oh? something broken af")

        val video = rt.getJSONArray("videos").getJSONObject(0)
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.reply {
            setAuthor("PornHub video search", video.getString("url"), "https://data.apkhere.com/b2/com.app.pornhub/4.1.1/icon.png!s")
            setTitle(video.getString("title"), video.getString("url"))
            setDescription("PornTube video search")
            setColor(Colors.getEffectiveColor(ctx.member))
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
