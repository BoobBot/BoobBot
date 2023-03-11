package bot.boobbot.entities.internals

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class CountingThreadFactory(private val name: String) : ThreadFactory {
    private val threadCounter = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread {
        return Thread(r, "$name-${threadCounter.getAndIncrement()}")
    }
}
