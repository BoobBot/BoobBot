package bot.boobbot.flight

import bot.boobbot.BoobBot
import net.dv8tion.jda.core.entities.Message
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import kotlin.concurrent.schedule
import kotlin.concurrent.timer

class WaitingEvent(
    private val id: Int,
    private val predicate: (Message) -> Boolean,
    private val future: CompletableFuture<Message?>
) {

    private val task: TimerTask = Timer().schedule(60000) {
        accept(null)
    }

    fun check(message: Message) = predicate(message)

    fun accept(message: Message?) {
        BoobBot.log.debug("Waiting-Event-$id completed with message $message")
        future.complete(message)

        BoobBot.waiter.remove(this)
        task.cancel()
    }

}
