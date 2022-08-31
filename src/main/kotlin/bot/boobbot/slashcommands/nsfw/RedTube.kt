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
    description = "RedTube video search.",
    guildOnly = true,
    aliases = ["rt"],
    nsfw = true,
    category = Category.VIDEOSEARCHING,
    donorOnly = true
)
class RedTube : AsyncSlashCommand {
    private fun urlFor(query: String): String {
        return "https://api.redtube.com/?data=redtube.Videos.searchVideos&output=json&thumbsize=big&ordering=mostviewed&page=1&search=$query"
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

        val video = rt.getJSONArray("videos").getJSONObject(0).getJSONObject("video")
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
                    "RedTube video search",
                    video.getString("embed_url"),
                    "https://cdn.discordapp.com/attachments/440667148315262978/490353839577497623/rt.png"
                )
                setTitle(video.getString("title"), video.getString("url"))
                setDescription("RedTube video search")
                setColor(Colors.rndColor)
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
                build()
            }.build()
        ).queue()
    }
}
