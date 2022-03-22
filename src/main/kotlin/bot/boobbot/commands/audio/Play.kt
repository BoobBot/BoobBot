package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import bot.boobbot.utils.toUriOrNull
import bot.boobbot.entities.framework.VoiceCommand

@CommandProperties(
    description = "Plays from a PornHub or RedTube URL (and YouTube if Donor)",
    category = Category.AUDIO,
    guildOnly = true,
    nsfw = true
)
class Play : VoiceCommand {
    override fun execute(ctx: Context) {
        if (ctx.args.firstOrNull()?.isEmpty() != false) {
            return ctx.send("Specify something to play, whore.\nSupported sites: `pornhub`, `redtube`, `youtube`")
        }

        if (!performVoiceChecks(ctx)) {
            return
        }

        val query = ctx.args[0].removeSurrounding("<", ">")

        if (!Utils.checkDonor(ctx.message) && isYouTubeTrack(query)) {
            return ctx.send(
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
