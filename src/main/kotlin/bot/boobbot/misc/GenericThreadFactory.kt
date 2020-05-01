package bot.boobbot.misc

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class GenericThreadFactory(private val name: String) : ThreadFactory {
    private val threadCounter = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread {
        return Thread(r, "$name-${threadCounter.getAndIncrement()}")
    }
}
