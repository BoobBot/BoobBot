package bot.boobbot.flight

import bot.boobbot.BoobBot
import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.misc.thenException
import com.mewna.catnip.entity.builder.EmbedBuilder
import com.mewna.catnip.entity.channel.MessageChannel
import com.mewna.catnip.entity.channel.TextChannel
import com.mewna.catnip.entity.guild.Guild
import com.mewna.catnip.entity.guild.Member
import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import com.mewna.catnip.entity.message.MessageOptions
import com.mewna.catnip.entity.user.User
import com.mewna.catnip.entity.user.VoiceState
import com.mewna.catnip.entity.util.Permission
import kotlinx.coroutines.future.await
import java.util.regex.Pattern

class Context(val trigger: String, val message: Message, val args: Array<String>) {
    val catnip = message.catnip()

    val guild: Guild? = message.guild()
    //val audioManager: AudioManager? = guild?.audioManager

    val selfUser: User? = catnip.selfUser()
    val selfMember: Member? = guild?.selfMember()

    val author: User = message.author()
    val member: Member? = message.member()
    val voiceState: VoiceState? = if (guild != null && member != null) catnip.cache().voiceState(guild.id(), member.id()) else null

    val channel: MessageChannel = message.channel()
    val textChannel: TextChannel? = if (channel.isText) channel.asTextChannel() else null

    val audioPlayer: GuildMusicManager?
        get() = if (guild == null) null else BoobBot.getMusicManager(guild)

    val mentionedChannels: List<TextChannel>

    init {
        val matcher = channelMention.matcher(message.content())
        val ids = mutableListOf<String>()

        while (matcher.find()) {
            ids.add(matcher.group(1))
        }

        mentionedChannels = ids.mapNotNull { guild?.channel(it)?.asTextChannel() }.toList()
    }

    fun userCan(check: Permission): Boolean {
        return channel.isDM ||
                textChannel != null && member?.hasPermissions(textChannel, check) ?: false
    }

    fun botCan(check: Permission): Boolean {
        return channel.isDM ||
                textChannel != null && selfMember?.hasPermissions(textChannel, check) ?: false
    }

    fun dm(embed: Embed) {
        val msg = MessageOptions()
            .embed(embed)
            .buildMessage()

        dm(msg)
    }

    private fun dm(message: Message) {
        author.createDM().thenAccept {
            it.sendMessage(message)
        }
    }

    fun send(content: String, success: ((Message) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        send(MessageOptions().content(content), success, failure)
    }

    suspend fun sendAsync(content: String): Message {
        return channel.sendMessage(content).await()
    }

    fun embed(block: EmbedBuilder.() -> Unit) {
        val builder = MessageOptions()
            .embed(EmbedBuilder().apply(block).build())

        send(builder, null, null)
    }

    fun embed(e: Embed) {
        send(MessageOptions().embed(e), null, null)
    }

    suspend fun dmUserAsync(user: User, message: String): Message? {
        val channel = user.createDM().await()
        return channel.sendMessage(message).await()
    }

    private fun send(message: MessageOptions, success: ((Message) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        val f = channel.sendMessage(message.buildMessage())

        if (success != null) {
            f.thenAccept(success)
        }

        if (failure != null) {
            f.thenException(failure)
        }
    }

    companion object {
        private val channelMention = Pattern.compile("<#(\\d{17,21})>")
    }

}
