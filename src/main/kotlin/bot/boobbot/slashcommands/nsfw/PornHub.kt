package bot.boobbot.slashcommands.nsfw

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.internal.JDAImpl
import net.dv8tion.jda.internal.entities.UserImpl
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
class PornHub : AsyncSlashCommand {
    private fun urlFor(query: String): String {
        return "https://www.pornhub.com/webmasters/search?search=$query&output=json${(0..99999).random()}"
    }

    override suspend fun executeAsync(event: SlashCommandInteractionEvent) {
        if (event.getOption("query")!!.asString.isEmpty()) {
            return event.replyEmbeds(
                EmbedBuilder().apply {
                    setColor(Color.red)
                    setDescription(Formats.error("Missing Args\nbbrt <tag> or random\n"))
                }.build()
            ).queue()
        }


        val query =
            if (event.getOption("query")!!.asString != "random") event.getOption("query")!!.asString else Formats.tag.random()

        val rt = BoobBot.requestUtil.get(urlFor(query)).await()?.json()
            ?: return event.reply("\uD83D\uDEAB oh? something broken af").queue()

        val video = rt.getJSONArray("videos").getJSONObject(0)


        val anonymousUser: User = UserImpl(0L, event.jda as JDAImpl)
            .setBot(false)
            .setName("Hidden User")
            .setDiscriminator("0000")
        var requester = event.user
        if (BoobBot.database.getUserAnonymity(event.user.id)) {
            requester = anonymousUser
        }

        event.replyEmbeds(
            EmbedBuilder().apply {
                setAuthor(
                    "PornHub video search",
                    video.getString("url"),
                    "https://data.apkhere.com/b2/com.app.pornhub/4.1.1/icon.png!s"
                )
                setTitle(video.getString("title"), video.getString("url"))
                setDescription("PornTube video search")
                setColor(Colors.rndColor)
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
            }.build()
        ).queue()
    }
}

