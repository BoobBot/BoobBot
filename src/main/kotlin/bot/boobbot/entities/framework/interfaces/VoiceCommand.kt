package bot.boobbot.entities.framework.interfaces

import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*

interface VoiceCommand : Command {
    fun isAlone(member: Member) = member.voiceState?.channel?.members?.count { !it.user.isBot } == 1
    fun isDJ(member: Member) = member.roles.any { it.name.equals("dj", true) }

    fun canSkip(ctx: MessageContext): Boolean {
        val user = ctx.audioPlayer.player.playingTrack.userData as User

        return ctx.userCan(Permission.MESSAGE_MANAGE)
                || ctx.user.idLong == user.idLong
                || Config.OWNERS.contains(ctx.user.idLong)
                || isDJ(ctx.member!!)
                || ctx.member.voiceState!!.channel!!.members.filter { !it.user.isBot }.size == 1
    }

    fun performVoiceChecks(ctx: MessageContext, nsfwCheck: Boolean = false): Boolean {
        if (ctx.guild == null) {
            return false
        }

        val audioManager = ctx.audioManager!!
        val memberVoice = ctx.voiceState!!
        val voiceChannel = memberVoice.channel

        if (voiceChannel == null || voiceChannel.type != ChannelType.VOICE) {
            ctx.reply(Formats.error("Join a voice-channel, whore"))
            return false
        }

        if (audioManager.connectedChannel == null) {
            val error = checkVoiceChannelPermissions(voiceChannel, nsfwCheck)

            return if (error == null) {
                audioManager.openAudioConnection(memberVoice.channel)
                true
            } else {
                ctx.reply(Formats.error(error))
                false
            }
        }

        if (voiceChannel.idLong != audioManager.connectedChannel!!.idLong) {
            ctx.reply(Formats.error("You gotta be in my voice-channel, whore."))
            return false
        }

        return true
    }

    fun checkVoiceChannelPermissions(channel: AudioChannel, nsfwCheck: Boolean): String? {
        val self = channel.guild.selfMember

        return when {
            channel is VoiceChannel && channel.userLimit > 0 && channel.members.size >= channel.userLimit && !self.hasPermission(channel, Permission.VOICE_MOVE_OTHERS)
                -> "There's no room in your voice-channel, raise the user limit"
            !self.hasPermission(channel, Permission.VOICE_CONNECT) -> "No slut, I don't have permissions to connect. fucking fix it!"
            !self.hasPermission(channel, Permission.VOICE_SPEAK) -> "No slut, I can't play music if I can't speak in your voice-channel"
            nsfwCheck && (channel !is VoiceChannel || !channel.isNSFW) -> "I can only play in age-restricted voice-channels, whore."
            else -> null
        }
    }
}
