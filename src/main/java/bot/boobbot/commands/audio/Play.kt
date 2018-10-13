package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.audio.AudioLoader
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import bot.boobbot.models.VoiceCommand
import java.util.regex.Pattern

@CommandProperties(description = "Plays from a PornHub or RedTube URL", category = Category.AUDIO, guildOnly = true, nsfw = true)
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
        val YT_REGEX = Pattern.compile("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+\$")
        val match = YT_REGEX.matcher(query)
        if (match.find()) {
            if (!Utils.isDonor(ctx.author)) {
                ctx.message.channel.sendMessage(Formats.error(
                        " Sorry YouTube music is only available to our Patrons.\n"
                                + ctx.jda
                                .asBot()
                                .shardManager
                                .getEmoteById(475801484282429450L)
                                .asMention
                                + "Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot")).queue()
                return
            }
        }

        playerManager.loadItem(query, AudioLoader(player, ctx))
        // if (ctx.botCan(Permission.MESSAGE_MANAGE)) {
        //ctx.message.delete().reason("no spam").queueAfter(5, TimeUnit.SECONDS)
        //}
    }

}
