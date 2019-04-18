package bot.boobbot.flight

import bot.boobbot.BoobBot
import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.models.Config
import kotlinx.coroutines.future.await
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.managers.AudioManager
import java.util.concurrent.CompletableFuture

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
        return if (channel.type == ChannelType.PRIVATE || Config.owners.contains(user.idLong)) {
            true
        } else {
            guild!!.getMember(user).hasPermission(channel as TextChannel, *permissions)
        }
    }

    fun userCan(check: Permission): Boolean {
        return permissionCheck(author, channel, check)
    }

    fun botCan(vararg check: Permission): Boolean {
        return permissionCheck(selfUser, channel, *check)
    }

    fun dm(embed: MessageEmbed) {
        val msg = MessageBuilder()
            .setEmbed(embed)
            .build()

        dm(msg)
    }

    private fun dm(message: Message) {
        author.openPrivateChannel().submit().thenAccept {
            it.sendMessage(message).queue()
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
        return try {
            val channel = user.openPrivateChannel().submit().await()
            channel.sendMessage(message).submit().await()
        } catch (e: Exception) {
            null
        }
    }

    fun awaitMessage(predicate: (Message) -> Boolean, timeout: Long): CompletableFuture<Message?> {
        return BoobBot.waiter.waitForMessage(predicate, timeout)
    }

    private fun send(message: MessageBuilder, success: ((Message) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        if (!botCan(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)) {
            return
            // Don't you just love it when people deny the bot
            // access to a channel during command execution?
            // https://sentry.io/share/issue/17c4b131f5ed48a6ac56c35ca276e4bf/
        }

        channel.sendMessage(message.build()).queue(success, failure)
    }

}
