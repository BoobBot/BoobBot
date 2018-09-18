package bot.boobbot.flight

import bot.boobbot.BoobBot
import bot.boobbot.misc.PendingEvent
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.managers.AudioManager

class Context(val trigger: String, val event: MessageReceivedEvent, val args: Array<String>) {
    val jda: JDA = event.jda
    val message: Message = event.message

    val guild: Guild? = event.guild
    val audioManager: AudioManager? = event.guild.audioManager

    val selfMember: Member? = event.guild.selfMember
    val selfUser: SelfUser = event.jda.selfUser

    val member: Member? = event.member
    val author: User = event.author
    val voiceState: VoiceState? = event.member.voiceState

    val channel: MessageChannel = event.channel
    val textChannel: TextChannel? = event.textChannel

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

    fun waitForMessage(predicate: (Message) -> Boolean = { true }, time: Long = 10000): PendingEvent {
        return BoobBot.getWaiter().waitForMessage(channel.idLong, author.idLong, predicate, time)
    }

    fun dm(content: String) {
        val builder = MessageBuilder().setContent(content)
        dm(builder.build())
    }

    fun dm(embed: MessageEmbed) {
        val builder = MessageBuilder().setEmbed(embed)
        dm(builder.build())
    }

    private fun dm(message: Message) {
        author.openPrivateChannel().queue { channel ->
            channel.sendMessage(message).queue({
                channel.close()
            }, {
                channel.close()
            })
        }
    }

    fun send(content: String, success: ((Message) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        send(MessageBuilder(content), success, failure)
    }

    fun embed(block: EmbedBuilder.() -> Unit) {
        val builder = MessageBuilder()
                .setEmbed(EmbedBuilder()
                        .apply(block)
                        .build())

        send(builder, null, null)
    }

    fun embed(e: MessageEmbed) {
        send(MessageBuilder().setEmbed(e), null, null)
    }

    fun embed(block: EmbedBuilder.() -> Unit, success: ((Message) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        val builder = MessageBuilder()
                .setEmbed(EmbedBuilder()
                        //.setColor()
                        .apply(block)
                        .build())

        send(builder, success, failure)
    }

    private fun send(message: MessageBuilder, success: ((Message) -> Unit)?, failure: ((Throwable) -> Unit)?) {
        channel.sendMessage(message.build()).queue(success, failure)
    }

}
