package bot.boobbot.models

import bot.boobbot.flight.Command
import bot.boobbot.flight.Context
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.entities.VoiceChannel

interface VoiceCommand : Command {

    fun isDJ(member: Member): Boolean {
        return member.roles.stream().anyMatch { x -> x.name.equals("dj", true) }
    }

    fun canSkip(ctx: Context): Boolean {
        val user = ctx.audioPlayer!!.player.playingTrack.userData as User

        return ctx.userCan(Permission.MESSAGE_MANAGE)
                || ctx.author.idLong == user.idLong
                || Config.owners.contains(ctx.author.idLong)
                || isDJ(ctx.member!!)
                || ctx.member.voiceState.channel.members.filter { !it.user.isBot }.size == 1
    }

    fun performVoiceChecks(ctx: Context): Boolean {
        if (ctx.guild == null) {
            return false
        }

        if (ctx.voiceState!!.channel == null) {
            ctx.send("Join a voicechannel, whore")
            return false
        }

        if (ctx.audioManager!!.connectedChannel == null) {
            val error = checkVoiceChannelPermissions(ctx.voiceState.channel)

            return if (error == null) {
                ctx.audioManager.openAudioConnection(ctx.voiceState.channel)
                true
            } else {
                ctx.send(error)
                false
            }
        }

        if (ctx.voiceState.channel.idLong != ctx.audioManager.connectedChannel.idLong) {
            ctx.send("You gotta be in my voicechannel, whore.")
            return false
        }

        return true
    }

    fun checkVoiceChannelPermissions(channel: VoiceChannel): String? {
        val self = channel.guild.selfMember

        if (channel.userLimit != 0 && channel.members.size >= channel.userLimit &&
            !self.hasPermission(channel, Permission.VOICE_MOVE_OTHERS)
        ) {
            return "There's no room in your voicechannel, raise the user limit"
        }

        if (!self.hasPermission(channel, Permission.VOICE_CONNECT)) {
            return "No slut, I don't have permissions to connect. fucking fix it!"
        }

        if (!self.hasPermission(channel, Permission.VOICE_SPEAK)) {
            return "No slut, I can't play music if I can't speak in your voicechannel"
        }

        return null
    }

}