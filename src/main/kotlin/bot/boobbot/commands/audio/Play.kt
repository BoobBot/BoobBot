package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.interfaces.VoiceCommand
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import bot.boobbot.utils.toUriOrNull

@CommandProperties(
    description = "Plays music from supported sources.",
    category = Category.AUDIO,
    guildOnly = true,
    nsfw = true,
    groupByCategory = true
)
@Option(name = "query", description = "Thing to search for.")
class Play : VoiceCommand {
    override fun execute(ctx: Context) {
        val query = ctx.options.getOptionStringOrGather("query")?.removeSurrounding("<", ">")
            ?: return ctx.reply("Specify something to play, whore.\nSupported sites: `pornhub`, `redtube`, `soundcloud`")

        val nsfwCheck = "pornhub" in query.lowercase() || "redtube" in query.lowercase()

        if (!performVoiceChecks(ctx, nsfwCheck)) {
            return
        }

        if (!Utils.checkDonor(ctx) && isYouTubeTrack(query)) {
            return ctx.reply(
                Formats.error(
                    " Sorry YouTube music is only available to our Patrons.\n<:p_:475801484282429450> "
                            + "Stop being a cheap fuck and join today! https://www.patreon.com/OfficialBoobBot"
                )
            )
        }

        playerManager.loadItem(query, AudioLoader(ctx))
    }

    private fun isYouTubeTrack(query: String): Boolean {
        val uri = query.toUriOrNull()
        val domain = if (uri?.host?.startsWith("www.") == true) {
            uri.host.substring(4)
        } else {
            uri?.host
        }

        return query.startsWith("ytsearch:") ||
                domain?.let { it == "youtube.com" || domain == "youtu.be" } ?: false
    }
}
