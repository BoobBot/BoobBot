package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.entities.internals.Config
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.managers.AudioManager
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.util.concurrent.CompletableFuture

class Context(val trigger: String, val message: Message, val args: List<String>) {
    val triggerIsMention = BOT_MENTIONS.any { it == trigger }
    val channelType = message.channelType
    val isFromGuild = channelType.isGuild

    val client = message.jda
    val jda: JDA = message.jda
    val guild: Guild? = if (isFromGuild) message.guild else null
    val audioManager: AudioManager? = guild?.audioManager

    val selfUser = client.selfUser
    val selfMember = guild?.selfMember

    val author = message.author
    val member: Member? = message.member
    val voiceState = member?.voiceState

    val channel = message.channel
    val textChannel: TextChannel? = if (isFromGuild) message.textChannel else null
    val guildChannel: GuildChannel? = if (isFromGuild) guild!!.getGuildChannelById(channel.idLong) else null

    val guildData: bot.boobbot.entities.db.Guild by lazy { BoobBot.database.getGuild(guild!!.id) }
    val customPrefix: String? by lazy { if (isFromGuild) guildData.prefix else null }

    val audioPlayer: GuildMusicManager
        get() {
            check(guild != null) { "Cannot retrieve a GuildMusicManager when guild is null!" }
            return BoobBot.getMusicManager(guild)
        }

    val mentions: List<User>
        get() = message.mentions.users.apply { if (triggerIsMention) remove(message.jda.selfUser) }


    fun permissionCheck(u: User, m: Member?, channel: MessageChannel, vararg permissions: Permission): Boolean {
        return !isFromGuild || Config.OWNERS.contains(u.idLong) ||
                (m?.hasPermission(channel as TextChannel, *permissions) ?: false)
    }

    fun userCan(check: Permission): Boolean {
        return permissionCheck(author, member, channel, check)
    }

    fun botCan(vararg check: Permission): Boolean {
        return permissionCheck(selfUser, selfMember, channel, *check)
    }

    fun dm(embed: MessageEmbed) = dm(MessageBuilder(embed).build())

    fun dm(message: Message) {
        author.openPrivateChannel().queue {
            it.sendMessage(message).queue()
        }
    }

    fun reply(content: String) {
        send(MessageBuilder(content), {
            reference(message)
            failOnInvalidReply(false)
        }, null, null)
    }

    fun send(content: String, success: ((Message) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        send(MessageBuilder(content), success, failure)
    }

    suspend fun sendAsync(content: String): Message {
        return channel.sendMessage(content).submit().await()
    }

    fun embed(block: EmbedBuilder.() -> Unit) {
        val builder = MessageBuilder()
            .setEmbeds(EmbedBuilder().apply(block).build())

        send(builder, null, null)
    }

    fun embed(e: MessageEmbed) {
        send(MessageBuilder(e), null, null)
    }

    suspend fun dmUserAsync(user: User, message: String): Message? {
        return try {
            user.openPrivateChannel()
                .flatMap { it.sendMessage(message) }
                .submit()
                .await()
        } catch (e: Exception) {
            null
        }
    }

    fun awaitMessage(predicate: (Message) -> Boolean, timeout: Long): CompletableFuture<Message?> {
        return BoobBot.waiter.waitForMessage(predicate, timeout)
    }

    private fun send(message: MessageBuilder, success: ((Message) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        send(message, {}, success, failure)
    }

    private fun send(message: MessageBuilder, options: MessageAction.() -> Unit, success: ((Message) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        if (!botCan(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) {
            return
            // Don't you just love it when people deny the bot
            // access to a channel during command execution?
            // https://sentry.io/share/issue/17c4b131f5ed48a6ac56c35ca276e4bf/
        }

        channel.sendMessage(message.build()).apply(options).queue(success, failure)
    }

    companion object {
        val BOT_MENTIONS = listOf("<@${BoobBot.selfId}> ", "<@!${BoobBot.selfId}> ")
    }

}
