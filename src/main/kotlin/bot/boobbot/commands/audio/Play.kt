package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import bot.boobbot.models.VoiceCommand
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import java.util.regex.Pattern

@CommandProperties(
    description = "Plays from a PornHub or RedTube URL (and YouTube if Donor)",
    category = Category.AUDIO,
    guildOnly = true,
    nsfw = true
)
class Play : VoiceCommand {

    private val PROTOCOL_REGEX = "(?:http://|https://|)"
    private val DOMAIN_REGEX = "(?:www\\.|m\\.|music\\.|)youtube\\.com"
    private val SHORT_DOMAIN_REGEX = "(?:www\\.|)youtu\\.be"
    private val VIDEO_ID_REGEX = "(?<v>[a-zA-Z0-9_-]{11})"
    private val PLAYLIST_ID_REGEX = "(?<list>(PL|LL|FL|UU)[a-zA-Z0-9_-]+)"

    //private val YT_REGEX = Pattern.compile("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+\$")

    private val directVideoIdPattern = Pattern.compile("^$VIDEO_ID_REGEX$")

    private val checks = arrayListOf(
        directVideoIdPattern,
        Pattern.compile("^$PLAYLIST_ID_REGEX$"),
        Pattern.compile("^$PROTOCOL_REGEX$DOMAIN_REGEX/.*"),
        Pattern.compile("^$PROTOCOL_REGEX$SHORT_DOMAIN_REGEX/.*")
    )

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
            for (pattern in checks) {
                if (pattern.matcher(query).matches()) {
                    ctx.send(
                        Formats.error(
                            " Sorry YouTube music is only available to our Patrons.\n<:p_:475801484282429450> "
                                    + "Stop being a cheap fuck and join today! https://www.patreon.com/OfficialBoobBot"
                        )
                    )
                    return
                }
            }
        }

        playerManager.loadItem(query, AudioLoader(player, ctx))

    }

}
