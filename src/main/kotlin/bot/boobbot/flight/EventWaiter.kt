package bot.boobbot.flight

import com.mewna.catnip.entity.message.Message
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EventWaiter {

    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private val pendingEvents = hashSetOf<WaitingEvent>()

    public fun waitForMessage(predicate: (Message) -> Boolean, timeout: Long): CompletableFuture<Message?> {
        val future = CompletableFuture<Message?>()
        val we = WaitingEvent(predicate, future)

        pendingEvents.add(we)

        scheduler.schedule({
            if (pendingEvents.remove(we)) {
                we.accept(null)
            }
        }, timeout, TimeUnit.MILLISECONDS)

        return future
    }

    public fun checkMessage(message: Message) {
        val passed = pendingEvents.filter { it.check(message) }
        pendingEvents.removeAll(passed)
        passed.forEach { it.accept(message) }
    }

}
