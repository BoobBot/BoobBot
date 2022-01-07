package bot.boobbot.entities.framework

import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.VoiceChannel

interface VoiceCommand : Command {
    fun isAlone(member: Member) = member.voiceState?.channel?.members?.count { !it.user.isBot } == 1
    fun isDJ(member: Member) = member.roles.any { it.name.equals("dj", true) }

    fun canSkip(ctx: Context): Boolean {
        val user = ctx.audioPlayer.player.playingTrack.userData as User

        return ctx.userCan(Permission.MESSAGE_MANAGE)
                || ctx.author.idLong == user.idLong
                || Config.OWNERS.contains(ctx.author.idLong)
                || isDJ(ctx.member!!)
                || ctx.member.voiceState!!.channel!!.members.filter { !it.user.isBot }.size == 1
    }

    fun performVoiceChecks(ctx: Context): Boolean {
        if (ctx.guild == null) {
            return false
        }

        val memberVoice = ctx.voiceState!!.channel

        if (memberVoice == null || memberVoice.type != ChannelType.VOICE) {
            ctx.send(Formats.error("Join a voice-channel, whore"))
            return false
        }

        if (ctx.audioManager!!.connectedChannel == null) {
            val error = checkVoiceChannelPermissions(memberVoice as VoiceChannel)

            return if (error == null) {
                ctx.audioManager.openAudioConnection(ctx.voiceState.channel)
                true
            } else {
                ctx.send(Formats.error(error))
                false
            }
        }

        if (ctx.voiceState.channel!!.idLong != ctx.audioManager.connectedChannel!!.idLong) {
            ctx.send(Formats.error("You gotta be in my voice-channel, whore."))
            return false
        }

        return true
    }

    fun checkVoiceChannelPermissions(channel: VoiceChannel): String? {
        val self = channel.guild.selfMember

        if (channel.userLimit != 0 && channel.members.size >= channel.userLimit &&
            !self.hasPermission(channel, Permission.VOICE_MOVE_OTHERS)
        ) {
            return "There's no room in your voice-channel, raise the user limit"
        }

        if (!self.hasPermission(channel, Permission.VOICE_CONNECT)) {
            return "No slut, I don't have permissions to connect. fucking fix it!"
        }

        if (!self.hasPermission(channel, Permission.VOICE_SPEAK)) {
            return "No slut, I can't play music if I can't speak in your voice-channel"
        }

        return null
    }
}
