package bot.boobbot.misc

import kotlinx.coroutines.experimental.future.await
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

class EventWaiter : ListenerAdapter() {

    private val messageWaiters = ConcurrentHashMap<Long, PendingEvent>()

    fun waitForMessage(channelID: Long, userID: Long, predicate: (Message) -> Boolean = { true }, time: Long = 10000): PendingEvent {
        val identifier = channelID + userID
        val result = PendingEvent(predicate)

        messageWaiters[identifier] = result

        Timer().schedule(time) {
            messageWaiters.remove(identifier)?.complete(null)
        }

        return result
    }

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
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

}

class PendingEvent(val predicate: (Message) -> Boolean) {

    private val future = CompletableFuture<Message?>()

    fun complete(message: Message?) {
        future.complete(message)
    }

    fun queue(callback: (Message?) -> Unit) {
        future.thenAcceptAsync(callback)
    }

    suspend fun await(): Message? {
        return future.await()
    }

}
