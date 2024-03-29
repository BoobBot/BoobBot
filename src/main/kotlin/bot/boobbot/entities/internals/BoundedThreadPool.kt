package bot.boobbot.entities.internals

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class BoundedThreadPool(name: String, maxThreads: Int, threadLifetime: Long, taskQueueLimit: Int) : ThreadPoolExecutor(
    1, maxThreads, threadLifetime,
    TimeUnit.SECONDS, ArrayBlockingQueue(taskQueueLimit),
    CountingThreadFactory(name), DiscardPolicy()
)
