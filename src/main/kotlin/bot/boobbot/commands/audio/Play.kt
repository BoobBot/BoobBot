package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import bot.boobbot.misc.toURLOrNull
import bot.boobbot.misc.toUrlOrNull
import bot.boobbot.models.VoiceCommand
import java.util.regex.Pattern

@CommandProperties(
    description = "Plays from a PornHub or RedTube URL (and YouTube if Donor)",
    category = Category.AUDIO,
    guildOnly = true,
    nsfw = true
)
class Play : VoiceCommand {

    override fun execute(ctx: Context) {
        val shouldPlay = performVoiceChecks(ctx)

        if (!shouldPlay) {
            return
        }

        if (ctx.args.isEmpty() || ctx.args[0].isEmpty()) {
            return ctx.send("Gotta specify a link, whore")
        }

        val player = ctx.audioPlayer!!
        val query = ctx.args[0].replace("<", "").replace(">", "")

        if (!Utils.isDonor(ctx.author)) {
            val url = query.toUrlOrNull()

            if (url != null && (url.host == "youtube.com" || url.host == "youtu.be") ||
                    query.startsWith("ytsearch:")) {
                ctx.send(
                    Formats.error(
                        " Sorry YouTube music is only available to our Patrons.\n<:p_:475801484282429450> "
                                + "Stop being a cheap fuck and join today! https://www.patreon.com/OfficialBoobBot"
                    )
                )
                return
            }
        }

        playerManager.loadItem(query, AudioLoader(player, ctx))

    }

}
