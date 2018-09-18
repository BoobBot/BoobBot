package bot.boobbot.flight

import bot.boobbot.BoobBot
import bot.boobbot.misc.PendingEvent
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.MessageReceivedEvent

class Context(val trigger: String, val event: MessageReceivedEvent, args: Array<String>) {
    val jda = event.jda
    val message = event.message

    val guild = event.guild
    val audioManager = event.guild.audioManager

    val selfMember = event.guild.selfMember
    val selfUser = event.jda.selfUser

    val member = event.member
    val author = event.author
    val voiceState = event.member.voiceState

    val channel = event.channel

    fun waitForMessage(predicate: (Message) -> Boolean = { true }, time: Long = 10000): PendingEvent {
        return BoobBot.getWaiter().waitForMessage(channel.idLong, author.idLong, predicate, time)
    }

    fun send(content: String, success: ((Message) -> Unit)? = null, failure: ((Throwable) -> Unit)? = null) {
        send(MessageBuilder(content), success, failure)
    }

    fun embed(block: EmbedBuilder.() -> Unit) {
        val builder = MessageBuilder()
                .setEmbed(EmbedBuilder()
                        //.setColor()
                        .apply(block)
                        .build())

        send(builder, null, null)
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
