package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.getMusicManager
import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.AudioLoader
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils.Companion.connectToVoiceChannel
import bot.boobbot.misc.Utils.Companion.getRandomMoan
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.utils.PermissionUtil

@CommandProperties(description = "moans :tired_face:", nsfw = true, category = CommandProperties.category.AUDIO)
class Moan : Command {

    override fun execute(ctx: Context) {
        if (getMusicManager(ctx.message.guild).player.playingTrack != null) {
            return ctx.message.channel.sendMessage(Formats.error("I am already playing, go away!")).queue()
        }

        if (!ctx.message.member.voiceState.inVoiceChannel()) {
            return ctx.message.channel.sendMessage(Formats.error("No whore, You must join a voice channel to use the command.")).queue()
        }
        if (!PermissionUtil.checkPermission(ctx.message.member.voiceState.channel, ctx.selfMember, Permission.VOICE_CONNECT)) {
            return ctx.message.channel.sendMessage(Formats.error("No slut, I don't have permissions to connect. fucking fix it!")).queue()
        }

        val musicManager = getMusicManager(ctx.message.guild)
        connectToVoiceChannel(ctx.message)
        playerManager.loadItemOrdered(musicManager, getRandomMoan().toString(), AudioLoader(musicManager, ctx))
        ctx.message.delete().reason("no spam").submit()
        ctx.send(":tired_face:")
    }

}