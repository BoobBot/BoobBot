package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.entities.internals.Config
import bot.boobbot.entities.misc.DSLMessageBuilder
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.EmbedBuilder

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.managers.AudioManager

import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue
import java.util.concurrent.CompletableFuture

class Context(val trigger: String, val message: Message, val args: List<String>) {
    val jda = message.jda
    val selfUser = jda.selfUser

    val triggerIsMention = BOT_MENTIONS.any { it == trigger }
    val friendlyTrigger = triggerIsMention.ifTrue { "@${selfUser.name} " } ?: trigger

    val channelType = message.channelType
    val isFromGuild = channelType.isGuild

    val guild: Guild? = if (isFromGuild) message.guild else null
    val audioManager: AudioManager? = guild?.audioManager

    val selfMember = guild?.selfMember

    val author = message.author
    val member: Member? = message.member
    val voiceState = member?.voiceState

    val channel = message.channel
    val textChannel: TextChannel? = if (isFromGuild) message.channel.asTextChannel() else null // todo reconsider
    val guildChannel: GuildChannel? = if (isFromGuild) message.guildChannel else null

    val guildData: bot.boobbot.entities.db.Guild by lazy { BoobBot.database.getGuild(guild!!.id) }
    val customPrefix: String? by lazy { if (isFromGuild) guildData.prefix else null }

    val audioPlayer: GuildMusicManager
        get() {
            check(guild != null) { "Cannot retrieve a GuildMusicManager when guild is null!" }
            return BoobBot.getMusicManager(guild)
        }

    val mentions: List<User>
        get() = if (triggerIsMention) message.mentions.users.minus(message.jda.selfUser) else message.mentions.users


    fun permissionCheck(u: User, m: Member?, channel: GuildChannel, vararg permissions: Permission): Boolean {
        return !isFromGuild || Config.OWNERS.contains(u.idLong) || m?.hasPermission(channel, *permissions) == true
    }

    fun userCan(check: Permission) = permissionCheck(author, member, guildChannel!!, check)

    fun botCan(vararg check: Permission) = permissionCheck(selfUser, selfMember, guildChannel!!, *check)

    fun awaitMessage(predicate: (Message) -> Boolean, timeout: Long) = BoobBot.waiter.waitForMessage(predicate, timeout)

    fun awaitNonConcurrentButton(uniqueId: String, predicate: (ButtonInteractionEvent) -> Boolean, timeout: Long, cb: (ButtonInteractionEvent?) -> Unit): Boolean {
        return BoobBot.waiter.waitForButton(uniqueId, predicate, timeout, cb)
    }

    fun awaitNonConcurrentMenu(uniqueId: String, predicate: (GenericComponentInteractionCreateEvent) -> Boolean, timeout: Long, cb: (GenericComponentInteractionCreateEvent?) -> Unit): Boolean {
        return BoobBot.waiter.waitForMenu(uniqueId, predicate, timeout, cb)
    }

    fun dm(embed: MessageEmbed) = dm(MessageCreateBuilder().addEmbeds(embed).build())

    fun dm(message: MessageCreateData) = author.openPrivateChannel().queue { it.sendMessage(message).queue() }

    fun reply(content: String) {
        send(MessageCreateBuilder().setContent(content).build(), {
            failOnInvalidReply(false)
        }, null, null)
    }

    fun send(content: String, success: ((Message) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        send(MessageCreateBuilder().setContent(content), success, failure)
    }

    fun send(block: EmbedBuilder.() -> Unit) = send(MessageCreateBuilder().addEmbeds(EmbedBuilder().apply(block).build()))

    fun send(e: MessageEmbed) = send(MessageCreateBuilder().addEmbeds(e))

    fun message(m: DSLMessageBuilder.() -> Unit) = send(DSLMessageBuilder().apply(m).build(), {}, null, null)

    private fun send(message: MessageCreateBuilder, success: ((Message) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        send(message.build(), {}, success, failure)
    }

    private fun send(message: MessageCreateData, options: MessageCreateAction.() -> Unit, success: ((Message) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        if (!botCan(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) {
            return
            // Don't you just love it when people deny the bot
            // access to a channel during command execution?
            // https://sentry.io/share/issue/17c4b131f5ed48a6ac56c35ca276e4bf/
        }

        channel.sendMessage(message).apply(options).queue(success, failure)
    }

    suspend fun sendAsync(content: String): Message {
        return channel.sendMessage(MessageCreateBuilder().setContent(content).build()).submit().await()
    }

    suspend fun dmUserAsync(user: User, message: String): Message? {
        return try {
            user.openPrivateChannel()
                .flatMap { it.sendMessage(MessageCreateBuilder().setContent(message).build()) }
                .submit()
                .await()
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        val BOT_MENTIONS = listOf("<@${BoobBot.selfId}> ", "<@!${BoobBot.selfId}> ")
    }

}
