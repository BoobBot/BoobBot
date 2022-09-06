package bot.boobbot.entities.framework.utils

import bot.boobbot.BoobBot
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EventWaiter : EventListener {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()

    //    private val pendingEvents = hashSetOf<WaitingEvent>()
    private val pendingEvents = ConcurrentHashMap.newKeySet<MessageWaiter>()
    private val pendingButtonEvents = ConcurrentHashMap<String, ButtonWaiter>()
    private val pendingMenuEvents = ConcurrentHashMap<String, MenuWaiter>()

    fun waitForMessage(predicate: (Message) -> Boolean, timeout: Long): CompletableFuture<Message?> {
        val future = CompletableFuture<Message?>()
        val we = MessageWaiter(predicate, future)

        scheduler.schedule({
            if (pendingEvents.remove(we)) {
                we.accept(null)
            }
        }, timeout, TimeUnit.MILLISECONDS)

        pendingEvents.add(we)
        return future
    }

    fun waitForButton(uniqueId: String, predicate: (ButtonInteractionEvent) -> Boolean, timeout: Long, cb: (ButtonInteractionEvent?) -> Unit): Boolean {
        if (pendingButtonEvents.containsKey(uniqueId)) {
            return false
        }

        val future = CompletableFuture<ButtonInteractionEvent?>().also { it.thenAccept(cb) }
        val bw = ButtonWaiter(predicate, future)

        scheduler.schedule({
            if (pendingButtonEvents.containsKey(uniqueId)) {
                pendingButtonEvents.remove(uniqueId)?.accept(null)
            }
        }, timeout, TimeUnit.MILLISECONDS)

        pendingButtonEvents[uniqueId] = bw
        return true
    }

    fun waitForMenu(uniqueId: String, predicate: (GenericComponentInteractionCreateEvent) -> Boolean, timeout: Long, cb: (GenericComponentInteractionCreateEvent?) -> Unit): Boolean {
        if (pendingMenuEvents.containsKey(uniqueId)) {
            return false
        }

        val future = CompletableFuture<GenericComponentInteractionCreateEvent?>().also { it.thenAccept(cb) }
        val mw = MenuWaiter(predicate, future)

        scheduler.schedule({
            if (pendingMenuEvents.containsKey(uniqueId)) {
                pendingMenuEvents.remove(uniqueId)?.accept(null)
            }
        }, timeout, TimeUnit.MILLISECONDS)

        pendingMenuEvents[uniqueId] = mw
        return true
    }

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is MessageReceivedEvent -> onMessageReceived(event)
            is ButtonInteractionEvent -> onButtonInteraction(event)
            is SelectMenuInteractionEvent -> onSelectMenuInteraction(event)
        }
    }

    private fun onMessageReceived(event: MessageReceivedEvent) {
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

    private fun onButtonInteraction(event: ButtonInteractionEvent) = checkAndRemove(pendingButtonEvents, event)
        .also { checkAndRemove(pendingMenuEvents, event) }

    private fun onSelectMenuInteraction(event: SelectMenuInteractionEvent) = checkAndRemove(pendingMenuEvents, event)

    private fun <E : GenericEvent, W : WaitingEvent<E>> checkAndRemove(map: ConcurrentHashMap<String, W>, event: E) {
        for ((k, v) in map) {
            try {
                if (v.check(event)) {
                    v.accept(event)
                    map.remove(k)
                }
            } catch (e: Exception) {
                BoobBot.log.error("Error in EventWaiter while checking waiter", e)
            }
        }
    }
}
