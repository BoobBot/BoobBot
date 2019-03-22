package bot.boobbot.flight

import bot.boobbot.BoobBot
import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.misc.await
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.managers.AudioManager
import java.util.concurrent.CompletableFuture

class Context(val trigger: String, val event: MessageReceivedEvent, val args: Array<String>) {
    val jda: JDA = event.jda
    val message: Message = event.message

    val guild: Guild? = event.guild
    val audioManager: AudioManager? = guild?.audioManager

    val selfUser: SelfUser = event.jda.selfUser
    val selfMember: Member? = guild?.selfMember

    val author: User = event.author
    val member: Member? = event.member
    val voiceState: GuildVoiceState? = member?.voiceState

    val channel: MessageChannel = event.channel
    val textChannel: TextChannel? = event.textChannel

    val audioPlayer: GuildMusicManager?
        get() = if (guild == null) null else BoobBot.getMusicManager(guild)


    fun userCan(check: Permission, explicit: Boolean = false): Boolean {
        if (!event.channelType.isGuild && !explicit) {
            return true
        }

        return member!!.hasPermission(event.textChannel, check)
    }

    fun botCan(check: Permission, explicit: Boolean = false): Boolean {
        if (!event.channelType.isGuild && !explicit) {
            return true
        }

        return selfMember!!.hasPermission(event.textChannel, check)
    }

    fun dm(embed: MessageEmbed) {
        val builder = MessageBuilder().setEmbed(embed)
        dm(builder.build())
    }

    private fun dm(message: Message) {
        author.openPrivateChannel().queue { channel ->
            channel.sendMessage(message).queue()
        }
    }

    fun send(content: String, success: ((Message) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        send(MessageBuilder(content), success, failure)
    }

    fun embed(block: EmbedBuilder.() -> Unit) {
        val builder = MessageBuilder()
            .setEmbed(
                EmbedBuilder()
                    .apply(block)
                    .build()
            )

        send(builder, null, null)
    }

    fun embed(e: MessageEmbed) {
        send(MessageBuilder().setEmbed(e), null, null)
    }

    suspend fun dmUserAsync(user: User, message: String): Message? {
        val privateChannel = user.openPrivateChannel().await()
            ?: return null

        return privateChannel.sendMessage(message).await()
    }

    private fun send(message: MessageBuilder, success: ((Message) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        return channel.sendMessage(message.build()).queue(success, failure)
    }

}
