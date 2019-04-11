package bot.boobbot.flight

import bot.boobbot.BoobBot
import net.dv8tion.jda.core.entities.Message
import java.util.concurrent.CompletableFuture

class WaitingEvent(
    private val id: Int,
    private val predicate: (Message) -> Boolean,
    private val future: CompletableFuture<Message?>
) {

    fun check(message: Message): Boolean {
        BoobBot.log.debug("Waiting-Event-$id checking message $message")
        val r = predicate(message)
        BoobBot.log.debug("Waiting-Event-$id predicate check returned $r")
        return r
    }

    fun accept(message: Message?) {
        BoobBot.log.debug("Waiting-Event-$id completed with message $message")
        future.complete(message)
    }

}
