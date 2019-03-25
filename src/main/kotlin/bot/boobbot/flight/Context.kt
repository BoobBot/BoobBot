package bot.boobbot.flight

import bot.boobbot.BoobBot
import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.misc.thenException
import kotlinx.coroutines.future.await
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.managers.AudioManager
import java.util.regex.Pattern

class Context(val trigger: String, val message: Message, val args: Array<String>) {
    val client = message.jda
    val jda: JDA = message.jda
    val guild: Guild? = message.guild
    val audioManager: AudioManager? = guild?.audioManager

    val selfUser = client.selfUser
    val selfMember = guild?.selfMember

    val author = message.author
    val member: Member? = message.member
    val voiceState = member?.voiceState

    val channel = message.channel
    val textChannel: TextChannel? = message.textChannel

    val audioPlayer: GuildMusicManager?
        get() = if (guild == null) null else BoobBot.getMusicManager(guild)


    fun permissionCheck(user: User, channel: MessageChannel, vararg permissions: Permission): Boolean {
        return if (channel.type == ChannelType.PRIVATE) {
            true
        } else {
            guild!!.getMember(user).hasPermission(channel as TextChannel, *permissions)
        }
    }

    fun userCan(check: Permission): Boolean {
        return permissionCheck(author, channel, check)
    }

    fun botCan(check: Permission): Boolean {
        return permissionCheck(selfUser, channel, check)
    }

    fun dm(embed: MessageEmbed) {
        val msg = MessageBuilder()
            .setEmbed(embed)
            .build()

        dm(msg)
    }

    private fun dm(message: Message) {
        author.openPrivateChannel().submit().thenAccept {
            it.sendMessage(message)
        }
    }

    fun send(content: String, success: ((Message) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        send(MessageBuilder().setContent(content), success, failure)
    }

    suspend fun sendAsync(content: String): Message {
        return channel.sendMessage(content).submit().await()
    }

    fun embed(block: EmbedBuilder.() -> Unit) {
        val builder = MessageBuilder()
            .setEmbed(EmbedBuilder().apply(block).build())

        send(builder, null, null)
    }

    fun embed(e: MessageEmbed) {
        send(MessageBuilder().setEmbed(e), null, null)
    }

    suspend fun dmUserAsync(user: User, message: String): Message? {
        val channel = user.openPrivateChannel().submit().await()
        return channel.sendMessage(message).submit().await()
    }

    private fun send(message: MessageBuilder, success: ((Message) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        channel.sendMessage(message.build()).queue(success, failure)
    }

    companion object {
        private val channelMention = Pattern.compile("<#(\\d{17,21})>")
    }

}
