package bot.boobbot.internals

import com.neovisionaries.ws.client.OpeningHandshakeException
import net.dv8tion.jda.api.utils.SessionController
import net.dv8tion.jda.api.utils.SessionControllerAdapter
import java.util.concurrent.TimeUnit

class CustomSessionController : SessionControllerAdapter() {

    override fun runWorker() {
        synchronized(lock) {
            if (workerHandle == null) {
                workerHandle = QueueWorker()
                workerHandle.start()
            }
        }
    }

    inner class QueueWorker(
        private val delay: Long
    ) : Thread("SessionControllerAdapter-Worker") {

        @JvmOverloads
        constructor(delay: Int = SessionController.IDENTIFY_DELAY) : this(TimeUnit.SECONDS.toMillis(delay.toLong()))

        init {
            super.setUncaughtExceptionHandler { thread, exception -> this.handleFailure(thread, exception) }
        }

        private fun handleFailure(thread: Thread, exception: Throwable) {
            log.error("Worker has failed with throwable!", exception)
        }

        override fun run() {
            try {
                if (this.delay > 0) {
                    val interval = System.currentTimeMillis() - lastConnect
                    if (interval < this.delay)
                        sleep(this.delay - interval)
                }
            } catch (ex: InterruptedException) {
                log.error("Unable to backoff", ex)
            }

            processQueue()
            synchronized(lock) {
                workerHandle = null
                if (!connectQueue.isEmpty())
                    runWorker()
            }
        }

        private fun processQueue() {
            var isMultiple = connectQueue.size > 1
            while (!connectQueue.isEmpty()) {
                val node = connectQueue.poll()
                try {
                    node.run(isMultiple && connectQueue.isEmpty())
                    isMultiple = true
                    lastConnect = System.currentTimeMillis()
                    if (connectQueue.isEmpty())
                        break
                    if (this.delay > 0)
                        sleep(this.delay)
                } catch (e: IllegalStateException) {
                    val t = e.cause
                    if (t is OpeningHandshakeException)
                        log.error("Failed opening handshake, appending to queue. Message: {}", e.message)
                    else
                        log.error("Failed to establish connection for a node, appending to queue", e)
                    appendSession(node)
                } catch (e: InterruptedException) {
                    log.error("Failed to run node", e)
                    appendSession(node)
                    return  // caller should start a new thread
                }
            }
        }
    }

}
