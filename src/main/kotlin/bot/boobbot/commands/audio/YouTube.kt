package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Formats
import bot.boobbot.entities.framework.VoiceCommand

@CommandProperties(
    description = "Searches YouTube for videos to play <:p_:475801484282429450> ",
    aliases = ["yt"],
    nsfw = false,
    category = Category.AUDIO,
    guildOnly = true,
    donorOnly = true
)
class YouTube : VoiceCommand {

    override fun execute(ctx: Context) {
        val shouldPlay = performVoiceChecks(ctx)

        if (!shouldPlay) {
            return
        }

        if (ctx.args.isEmpty() || ctx.args[0].isEmpty()) {
            return ctx.send(Formats.error("Gotta specify a search query, whore"))
        }

        val player = ctx.audioPlayer
        val query = "ytsearch:${ctx.args.joinToString(" ")}"

        playerManager.loadItem(query, AudioLoader(player, ctx))

    }
}
