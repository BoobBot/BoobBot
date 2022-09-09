package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.interfaces.VoiceCommand
import bot.boobbot.utils.Formats

@CommandProperties(
    description = "Searches YouTube for videos to play",
    aliases = ["yt"],
    category = Category.AUDIO,
    guildOnly = true,
    donorOnly = true,
    groupByCategory = true
)
class YouTube : VoiceCommand {
    override fun execute(ctx: Context) {
        if (!performVoiceChecks(ctx)) {
            return
        }

        val query = ctx.options.getOptionStringOrGather("query")
            ?: return ctx.reply(Formats.error("Gotta specify a search query, whore"))

        playerManager.loadItem("ytsearch:$query", AudioLoader(ctx))
    }
}
