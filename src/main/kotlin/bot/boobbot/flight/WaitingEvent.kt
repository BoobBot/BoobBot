package bot.boobbot.flight

import bot.boobbot.BoobBot
import net.dv8tion.jda.core.entities.Message
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.schedule

class WaitingEvent(
    private val id: Int,
    private val predicate: (Message) -> Boolean,
    private val future: CompletableFuture<Message?>
) {

    fun check(message: Message) = predicate(message)

    fun accept(message: Message?) {
        BoobBot.log.debug("Waiting-Event-$id completed with message $message")
        future.complete(message)

        //BoobBot.waiter.remove(this)
    }

}
