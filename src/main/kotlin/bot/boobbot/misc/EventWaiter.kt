package bot.boobbot.misc

import kotlinx.coroutines.future.await
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageReaction
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionAddEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

class EventWaiter : ListenerAdapter() {

    private val messageWaiters = ConcurrentHashMap<Long, PendingEvent<Message>>()
    private val reactionWaiters = ConcurrentHashMap<Long, PendingEvent<MessageReaction.ReactionEmote>>()

    fun waitForMessage(
        channelID: Long,
        userID: Long,
        predicate: (Message) -> Boolean = { true },
        time: Long = 10000
    ): PendingEvent<Message> {
        val identifier = channelID + userID
        val result = PendingEvent(predicate)

        messageWaiters[identifier] = result

        Timer().schedule(time) {
            messageWaiters.remove(identifier)?.complete(null)
        }

        return result
    }

    fun waitForReaction(
        channelID: Long,
        userID: Long,
        predicate: (MessageReaction.ReactionEmote) -> Boolean = { true },
        time: Long = 10000
    ):
            PendingEvent<MessageReaction.ReactionEmote> {
        val identifier = channelID + userID
        val result = PendingEvent(predicate)

        reactionWaiters[identifier] = result

        Timer().schedule(time) {
            reactionWaiters.remove(identifier)?.complete(null)
        }

        return result
    }

    override fun onMessageReceived(e: MessageReceivedEvent) {
        val identifier = e.channel.idLong + e.author.idLong

        if (!messageWaiters.containsKey(identifier)) {
            return
        }

        val waiter = messageWaiters[identifier]!!
        val predicateMatch = waiter.predicate(e.message)

        if (predicateMatch) {
            waiter.complete(e.message)
            messageWaiters.remove(identifier)
        }
    }

    override fun onPrivateMessageReactionAdd(e: PrivateMessageReactionAddEvent) {
        val identifier = e.channel.idLong + e.user.idLong

        if (!reactionWaiters.containsKey(identifier)) {
            return
        }

        val waiter = reactionWaiters[identifier]!!
        val predicateMatch = waiter.predicate(e.reactionEmote)

        if (predicateMatch) {
            waiter.complete(e.reactionEmote)
            reactionWaiters.remove(identifier)
        }
    }

}

class PendingEvent<T>(val predicate: (T) -> Boolean) {

    private val future = CompletableFuture<T?>()

    fun complete(obj: T?) {
        future.complete(obj)
    }

    fun queue(callback: (T?) -> Unit) {
        future.thenAcceptAsync(callback)
    }

    suspend fun await(): T? {
        return future.await()
    }

}
