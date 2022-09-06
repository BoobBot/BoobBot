package bot.boobbot.entities.framework

import bot.boobbot.entities.framework.impl.SlashOptions
import bot.boobbot.entities.misc.DSLMessageBuilder
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData

class SlashContext(val event: SlashCommandInteractionEvent) : Context(false, mutableListOf(), true,event.jda, event.user, event.member, event.channel, event.takeIf { it.isFromGuild }?.guild) {
    var replied = false
        private set
    var deferred = false
        private set
    val subcommandName: String?
        get() = event.subcommandName

    override val options = SlashOptions(event.options)

    fun getOption(name: String): OptionMapping? = event.getOption(name)
    fun <T> getOption(name: String, resolver: (OptionMapping) -> T): T? = event.getOption(name, resolver)
    fun getOptionsByType(type: OptionType): List<OptionMapping> = event.getOptionsByType(type)

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

    override fun reply(content: String, ephemeral: Boolean) = message(ephemeral) { content(content) }

    override fun reply(file: FileUpload, ephemeral: Boolean) = message(ephemeral) { file(file) }

    fun reply(content: String, ephemeral: Boolean = false, success: ((InteractionHook) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        reply(MessageCreateData.fromContent(content), ephemeral, success, failure)
    }

    override fun reply(ephemeral: Boolean, embed: EmbedBuilder.() -> Unit) = message(ephemeral) { embed(embed) }

    fun reply(e: MessageEmbed, ephemeral: Boolean = false) = reply(MessageCreateData.fromEmbeds(e), ephemeral, null, null)

    fun message(ephemeral: Boolean = false, m: DSLMessageBuilder.() -> Unit) = reply(DSLMessageBuilder().apply(m).build(), ephemeral, null, null)

    fun reply(message: MessageCreateBuilder, ephemeral: Boolean = false, success: ((InteractionHook) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        reply(message.build(), ephemeral, success, failure)
    }

    private fun reply(message: MessageCreateData, ephemeral: Boolean = false, success: ((InteractionHook) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        if (!botCan(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) {
            return
        }

        when {
            replied -> event.hook.sendMessage(message).setEphemeral(ephemeral).queue({ replied = true; success?.invoke(event.hook) }, failure)
            deferred -> event.hook.editOriginal(MessageEditData.fromCreateData(message)).queue({ replied = true; success?.invoke(event.hook) }, failure)
            else -> event.reply(message).setEphemeral(ephemeral).queue({ replied = true; success?.invoke(it) }, failure)
        }
    }
}
