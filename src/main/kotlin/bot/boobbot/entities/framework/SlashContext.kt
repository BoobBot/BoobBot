package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.audio.GuildMusicManager
import bot.boobbot.entities.internals.Config
import bot.boobbot.entities.misc.DSLMessageBuilder
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.managers.AudioManager
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData

class SlashContext(val event: SlashCommandInteractionEvent) {
    var replied = false
        private set
    var deferred = false
        private set
    val subcommandName: String?
        get() = event.subcommandName
    val options: List<OptionMapping>
        get() = event.options

    val jda = event.jda
    val selfUser = jda.selfUser

    val channelType = event.channelType
    val isFromGuild = channelType.isGuild

    val guild: Guild? = if (isFromGuild) event.guild else null
    val audioManager: AudioManager? = guild?.audioManager

    val selfMember = guild?.selfMember

    val user = event.user
    val member: Member? = event.member
    val voiceState = member?.voiceState

    val channel = event.channel
    val textChannel: TextChannel? = if (channelType == ChannelType.TEXT) event.channel.asTextChannel() else null // todo reconsider
    val guildChannel: GuildChannel? = if (isFromGuild) event.guildChannel else null

    val guildData: bot.boobbot.entities.db.Guild by lazy { BoobBot.database.getGuild(guild!!.id) }
    val customPrefix: String? by lazy { if (isFromGuild) guildData.prefix else null }

    val audioPlayer: GuildMusicManager
        get() {
            check(guild != null) { "Cannot retrieve a GuildMusicManager when guild is null!" }
            return BoobBot.getMusicManager(guild)
        }

    fun permissionCheck(u: User, m: Member?, channel: GuildChannel, vararg permissions: Permission): Boolean {
        return !isFromGuild || Config.OWNERS.contains(u.idLong) || m?.hasPermission(channel, *permissions) == true
    }

    fun userCan(check: Permission) = guildChannel?.let { permissionCheck(user, member, guildChannel, check) } ?: false

    fun botCan(vararg check: Permission) = guildChannel?.let { permissionCheck(selfUser, selfMember, it, *check) } ?: true

    fun getOption(name: String): OptionMapping? = event.getOption(name)
    fun <T> getOption(name: String, resolver: (OptionMapping) -> T): T? = event.getOption(name, resolver)
    fun getOptionsByType(type: OptionType): List<OptionMapping> = event.getOptionsByType(type)

    fun awaitMessage(predicate: (Message) -> Boolean, timeout: Long) = BoobBot.waiter.waitForMessage(predicate, timeout)

    fun onButtonInteraction(uniqueId: String, predicate: (ButtonInteractionEvent) -> Boolean, timeout: Long, cb: (ButtonInteractionEvent?) -> Unit): Boolean {
        return BoobBot.waiter.waitForButton(uniqueId, predicate, timeout, cb)
    }

    fun onMenuInteraction(uniqueId: String, predicate: (GenericComponentInteractionCreateEvent) -> Boolean, timeout: Long, cb: (GenericComponentInteractionCreateEvent?) -> Unit): Boolean {
        return BoobBot.waiter.waitForMenu(uniqueId, predicate, timeout, cb)
    }

    fun defer(ephemeral: Boolean = false) {
        if (!deferred) { // Idempotency handling
            event.deferReply(ephemeral).queue { deferred = true }
        }
    }

    suspend fun deferAsync(ephemeral: Boolean = false) {
        if (!deferred) {
            event.deferReply(ephemeral).submit()
                .thenAccept { deferred = true }
                .await()
        }
    }

    fun dm(embed: MessageEmbed) = dm(MessageCreateBuilder().addEmbeds(embed).build())

    fun dm(message: MessageCreateData) = user.openPrivateChannel().queue { it.sendMessage(message).queue() }

    fun reply(content: String, ephemeral: Boolean = false) {
        reply(MessageCreateBuilder().setContent(content).build(), ephemeral, null, null)
    }

    fun reply(content: String, ephemeral: Boolean = false, success: ((InteractionHook) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        reply(MessageCreateBuilder().setContent(content), ephemeral, success, failure)
    }

    fun reply(ephemeral: Boolean = false, block: EmbedBuilder.() -> Unit) = reply(EmbedBuilder().apply(block).build(), ephemeral)

    fun reply(fileUpload: FileUpload, ephemeral: Boolean = false) = reply(MessageCreateBuilder().addFiles(fileUpload), ephemeral)

    fun reply(e: MessageEmbed, ephemeral: Boolean = false) = reply(MessageCreateBuilder().addEmbeds(e), ephemeral)

    fun message(ephemeral: Boolean = false, m: DSLMessageBuilder.() -> Unit) = reply(DSLMessageBuilder().apply(m).build(), ephemeral, null, null)

    fun reply(message: MessageCreateBuilder, ephemeral: Boolean = false, success: ((InteractionHook) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        reply(message.build(), ephemeral, success, failure)
    }

    private fun reply(message: MessageCreateData, ephemeral: Boolean = false, success: ((InteractionHook) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        if (!botCan(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) {
            return
        }

        when {
            replied -> event.hook.sendMessage(message).setEphemeral(ephemeral).submit()
            deferred -> event.hook.editOriginal(MessageEditData.fromCreateData(message)).submit().thenApply { replied = true }
            else -> event.reply(message).setEphemeral(ephemeral).queue({ replied = true; success?.invoke(it) }, failure)
        }
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
}
