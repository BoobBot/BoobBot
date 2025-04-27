package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.entities.framework.interfaces.Options
import bot.boobbot.entities.misc.DSLMessageBuilder
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.managers.AudioManager
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageCreateData

abstract class Context(val mentionTrigger: Boolean,
                       private val _mentions: MutableList<User>,
                       val isSlashContext: Boolean,
                       val jda: JDA,
                       val user: User,
                       val member: Member?,
                       val channel: MessageChannelUnion,
                       val _guild: Guild?) {
    val prefix = if (isSlashContext) "/" else "@${jda.selfUser.name} "
    val guildData = BoobBot.database.getGuild(guild.idLong).also {
        if (it.isNew) {
            println("creating new guild data")
            it.save()
        }
    }

    val selfUser = jda.selfUser
    val selfMember = _guild?.selfMember

    val channelType = channel.type
    val isFromGuild = channelType.isGuild

    val asSlash: SlashContext?
        get() = this as? SlashContext

    val asMessage: MessageContext?
        get() = this as? MessageContext

    val guild: Guild
        get() = _guild ?: throw IllegalStateException("Guild is not available.")

    val textChannel: TextChannel? = if (channelType == ChannelType.TEXT) channel.asTextChannel() else null
    val guildChannel: GuildChannel? = channel as? GuildChannel

    val voiceState: GuildVoiceState? get() = member?.voiceState

    val audioManager: AudioManager?
        get() = _guild?.audioManager

    val audioPlayer: GuildMusicManager
        get() {
            check(_guild != null) { "Cannot retrieve a GuildMusicManager when guild is null!" }
            return BoobBot.getOrCreateMusicManager(_guild)
        }

    val mentions: List<User>
        get() = _mentions.apply { if (mentionTrigger) minus(selfUser) }

    abstract val options: Options

    fun permissionCheck(u: User, m: Member?, channel: GuildChannel, vararg permissions: Permission): Boolean {
        return !isFromGuild || BoobBot.owners.contains(u.idLong) || m?.hasPermission(channel, *permissions) == true
    }

    fun userCan(check: Permission) = guildChannel?.let { permissionCheck(user, member, guildChannel, check) } ?: false

    fun botCan(vararg check: Permission) = guildChannel?.let { permissionCheck(selfUser, selfMember, it, *check) } ?: true

    fun awaitMessage(predicate: (Message) -> Boolean, timeout: Long) = BoobBot.waiter.waitForMessage(predicate, timeout)

    fun onButtonInteraction(uniqueId: String, predicate: (ButtonInteractionEvent) -> Boolean, timeout: Long, cb: (ButtonInteractionEvent?) -> Unit): Boolean {
        return BoobBot.waiter.waitForButton(uniqueId, predicate, timeout, cb)
    }

    fun onMenuInteraction(uniqueId: String, predicate: (GenericComponentInteractionCreateEvent) -> Boolean, timeout: Long, cb: (GenericComponentInteractionCreateEvent?) -> Unit): Boolean {
        return BoobBot.waiter.waitForMenu(uniqueId, predicate, timeout, cb)
    }

    fun dm(embed: MessageEmbed) = dm(MessageCreateData.fromEmbeds(embed))

    fun dm(message: MessageCreateData) = user.openPrivateChannel().queue { it.sendMessage(message).queue() }

    suspend fun dmUserAsync(user: User, message: String): Message? {
        return try {
            user.openPrivateChannel()
                .flatMap { it.sendMessage(MessageCreateData.fromContent(message)) }
                .submit()
                .await()
        } catch (e: Exception) {
            null
        }
    }

    open fun react(emoji: Emoji) {
        throw UnsupportedOperationException()
    }

    open suspend fun defer() = Unit

    abstract fun reply(content: String, ephemeral: Boolean = false)

    abstract fun reply(file: FileUpload, ephemeral: Boolean = false)

    abstract fun reply(files: List<FileUpload>, ephemeral: Boolean = false)

    abstract fun reply(embed: MessageEmbed, ephemeral: Boolean = false)

    abstract fun reply(ephemeral: Boolean = false, embed: EmbedBuilder.() -> Unit)

    abstract fun message(ephemeral: Boolean = false, message: DSLMessageBuilder.() -> Unit)

}
