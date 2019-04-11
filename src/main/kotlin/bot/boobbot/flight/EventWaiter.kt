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

    private val scheduler = Executors.newSingleThreadScheduledExecutor()

    public fun waitForMessage(predicate: (Message) -> Boolean, timeout: Long): CompletableFuture<Message?> {
        totalWaiters++

        val future = CompletableFuture<Message?>()
        val we = WaitingEvent(totalWaiters, predicate, future)

        scheduler.schedule({
            if (pendingEvents.remove(we)) {
                we.accept(null)
            }
        }, timeout, TimeUnit.MILLISECONDS)

        pendingEvents.add(we)
        return future
    }

    public override fun onMessageReceived(event: MessageReceivedEvent) {
        for (p in pendingEvents) {
            try {
                if (p.check(event.message)) {
                    p.accept(event.message)
                    pendingEvents.remove(p)
                }
            } catch (e: Exception) {
                BoobBot.log.error("Error in EventWaiter while checking message", e)
            }
        }
    }

}
