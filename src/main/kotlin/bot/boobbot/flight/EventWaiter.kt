package bot.boobbot.flight

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EventWaiter : ListenerAdapter() {

    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val pendingEvents = hashMapOf<Class<*>, HashSet<WaitingEvent<*>>>()

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
