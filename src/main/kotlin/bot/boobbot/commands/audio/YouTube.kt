package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.models.VoiceCommand

@CommandProperties(description = "Searches YouTube for videos to play", nsfw = false, category = Category.AUDIO, guildOnly = true, donorOnly = true)
class YouTube : VoiceCommand {

    override fun execute(ctx: Context) {
        val shouldPlay = performVoiceChecks(ctx)

        if (!shouldPlay) {
            return
        }

        if (ctx.args.isEmpty() || ctx.args[0].isEmpty()) {
            return ctx.send("Gotta specify a search query, whore")
        }

        val player = ctx.audioPlayer!!
        val query = "ytsearch:${ctx.args.joinToString(" ")}"

        playerManager.loadItem(query, AudioLoader(player, ctx))
        //if (ctx.botCan(Permission.MESSAGE_MANAGE)) {
        //ctx.message.delete().reason("no spam").queueAfter(5, TimeUnit.SECONDS)
        //}
    }
}
