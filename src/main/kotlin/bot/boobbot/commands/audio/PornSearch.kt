package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.interfaces.VoiceCommand

@CommandProperties(
    description = "Searches PornHub for videos to play",
    nsfw = true,
    category = Category.AUDIO,
    guildOnly = true
)
class PornSearch : VoiceCommand {
    override fun execute(ctx: Context) {
        if (!performVoiceChecks(ctx, nsfwCheck = true)) {
            return
        }

        val query = ctx.options.getOptionStringOrGather("query")
            ?: return ctx.reply("Gotta specify a search query, whore")

        playerManager.loadItem("phsearch:$query", AudioLoader(ctx))
    }
}
