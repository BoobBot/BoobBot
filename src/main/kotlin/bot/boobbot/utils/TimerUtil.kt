package bot.boobbot.utils

import bot.boobbot.BoobBot
import java.time.Duration
import java.util.concurrent.TimeUnit

class TimerUtil(private val identifier: String) {
    private val timeStart = System.nanoTime()

    fun elapsed() = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - timeStart)

    fun elapsedFormatted(): String {
        val duration = Duration.ofNanos(System.nanoTime() - timeStart)
        return "%02d:%02d:%02d.%03d".format(duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart(), duration.toMillisPart())
    }

    fun stop() {
        val elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - timeStart)
        BoobBot.log.info("[Timer:$identifier] Took ${elapsed}ms")
    }

    companion object {
        fun <T> inline(identifier: String, block: () -> T): T {
            val timer = TimerUtil(identifier)
            val r = block()
            timer.stop()
            return r
        }

        suspend fun <T> inlineSuspended(timerName: String, block: suspend () -> T): T {
            val timer = TimerUtil(timerName)
            val r = block()
            timer.stop()
            return r
        }
    }
}