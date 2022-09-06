package bot.boobbot.entities.framework.utils

import bot.boobbot.BoobBot
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import java.util.concurrent.CompletableFuture

abstract class WaitingEvent<T>(
    private val predicate: (T) -> Boolean,
    private val future: CompletableFuture<T?>
) {
    val id: Int = totalWaiters++

    fun check(obj: T): Boolean {
        BoobBot.log.debug("Waiting-Event-$id checking $obj")
        val r = predicate(obj)
        BoobBot.log.debug("Waiting-Event-$id predicate check returned $r")
        return r
    }

    fun accept(obj: T?) {
        BoobBot.log.debug("Waiting-Event-$id completed with obj $obj")
        future.complete(obj)
    }

    companion object {
        var totalWaiters = 0
    }
}

class MessageWaiter(predicate: (Message) -> Boolean, future: CompletableFuture<Message?>) : WaitingEvent<Message>(predicate, future)
class ButtonWaiter(predicate: (ButtonInteractionEvent) -> Boolean, future: CompletableFuture<ButtonInteractionEvent?>) : WaitingEvent<ButtonInteractionEvent>(predicate, future)
class MenuWaiter(predicate: (GenericComponentInteractionCreateEvent) -> Boolean, future: CompletableFuture<GenericComponentInteractionCreateEvent?>) : WaitingEvent<GenericComponentInteractionCreateEvent>(predicate, future)
