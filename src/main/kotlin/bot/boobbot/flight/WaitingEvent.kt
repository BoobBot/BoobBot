package bot.boobbot.flight

import net.dv8tion.jda.core.entities.Message
import java.util.concurrent.CompletableFuture

@Suppress("UNCHECKED_CAST")
class WaitingEvent(
    private val predicate: (Message) -> Boolean,
    private val future: CompletableFuture<Message?>
) {

    fun check(message: Message) = predicate(message)

    fun accept(message: Message?) = future.complete(message)

}
