package bot.boobbot.flight

import bot.boobbot.BoobBot
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EventWaiter : ListenerAdapter() {

    private var totalWaiters = 0
    private val pendingEvents = hashSetOf<WaitingEvent>()

    public fun remove(we: WaitingEvent) {
        pendingEvents.remove(we)
    }

    public fun waitForMessage(predicate: (Message) -> Boolean, timeout: Long): CompletableFuture<Message?> {
        totalWaiters++

        val future = CompletableFuture<Message?>()
        val we = WaitingEvent(totalWaiters, predicate, future)

        pendingEvents.add(we)
        return future
    }

    public override fun onMessageReceived(event: MessageReceivedEvent) {
        try {
            val passed = pendingEvents.filter { it.check(event.message) }
//            pendingEvents.removeAll(passed)
            passed.forEach { it.accept(event.message) }
        } catch (e: Exception) {
            BoobBot.log.error("Error in EventWaiter while checking message", e)
        }
    }

}
