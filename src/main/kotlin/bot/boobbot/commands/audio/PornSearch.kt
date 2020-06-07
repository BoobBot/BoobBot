package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.VoiceCommand

@CommandProperties(
    description = "Searches PornHub for videos to play",
    nsfw = true,
    category = Category.AUDIO,
    guildOnly = true
)
class PornSearch : VoiceCommand {

    override fun execute(ctx: Context) {
        val shouldPlay = performVoiceChecks(ctx)

        if (!shouldPlay) {
            return
        }

        if (ctx.args.isEmpty() || ctx.args[0].isEmpty()) {
            return ctx.send("Gotta specify a search query, whore")
        }

        val player = ctx.audioPlayer
        val query = "phsearch:${ctx.args.joinToString(" ")}"

        playerManager.loadItem(query, AudioLoader(player, ctx))

    }
}
