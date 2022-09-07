package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.impl.MessageOptions
import bot.boobbot.entities.misc.DSLMessageBuilder
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageCreateData

class MessageContext(val message: Message, val args: List<String>) : Context(true, message.mentions.users.toMutableList(), false, message.jda, message.author, message.member, message.channel, message.takeIf { it.isFromGuild }?.guild) {
    override val options = MessageOptions(args.toMutableList())

    private val defaultReplyOptions: MessageCreateAction.() -> Unit
        get() = {
            setMessageReference(message)
            failOnInvalidReply(false)
        }

    override fun reply(content: String, ephemeral: Boolean) = message({ content(content) }, defaultReplyOptions)

    override fun reply(ephemeral: Boolean, embed: EmbedBuilder.() -> Unit) = message({ embed(embed) }, defaultReplyOptions)

    override fun reply(file: FileUpload, ephemeral: Boolean) = message({ file(file) }, defaultReplyOptions)

    override fun reply(embed: MessageEmbed, ephemeral: Boolean) = message({ embed(embed) }, defaultReplyOptions)

    fun send(e: MessageEmbed) = message({ embed(e) })

    fun message(m: DSLMessageBuilder.() -> Unit, sendOptions: MessageCreateAction.() -> Unit = {}) = send(DSLMessageBuilder().apply(m).build(), sendOptions, null, null)

    private fun send(message: MessageCreateData, options: MessageCreateAction.() -> Unit, success: ((Message) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        if (!botCan(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) {
            return
        }

        channel.sendMessage(message).apply(options).queue(success, failure)
    }

    companion object {
        val BOT_MENTIONS = listOf("<@${BoobBot.selfId}>", "<@!${BoobBot.selfId}>")
    }

}
