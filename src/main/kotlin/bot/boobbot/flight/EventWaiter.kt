package bot.boobbot.flight

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet

class EventWaiter : ListenerAdapter() {

    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val pendingEvents = hashMapOf<Class<*>, HashSet<WaitingEvent<*>>>()
//    private val messageWaiters = ConcurrentHashMap<Long, PendingEvent<Message>>()
//    private val reactionWaiters = ConcurrentHashMap<Long, PendingEvent<MessageReaction.ReactionEmote>>()

    fun <T : Event> waitFor(event: Class<T>, predicate: (T) -> Boolean, timeout: Long): CompletableFuture<T?> {
        val future = CompletableFuture<T?>()
        val we = WaitingEvent(event, predicate, future)

        val set = pendingEvents.computeIfAbsent(event) { hashSetOf() }
        set.add(we)

        scheduler.schedule({ pendingEvents[event]?.remove(we) }, timeout, TimeUnit.MILLISECONDS)

        return future
    }

    override fun onGenericEvent(event: Event) {
        val cls = event::class.java

        if (pendingEvents.containsKey(cls)) {
            val events = pendingEvents[cls]!!
            val passed = events.filter { it.check(event) }

            events.removeAll(passed)
            passed.forEach { it.accept(event) }
        }
    }

}
