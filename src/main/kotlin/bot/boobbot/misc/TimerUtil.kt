package bot.boobbot.misc

import bot.boobbot.BoobBot

class TimerUtil(val identifier: String) {
    private val timeStart = System.currentTimeMillis()

    fun stop() {
        val timeEnd = System.currentTimeMillis()
        BoobBot.log.info("[Timer:$identifier] Took ${timeEnd - timeStart}ms")
    }

    companion object {
        fun <T> inline(identifier: String, block: () -> T): T {
            val timer = TimerUtil(identifier)
            val r = block()
            timer.stop()
            return r
        }

        suspend fun <T> inlineSuspended(timerName: String, block: suspend () -> T): T {
            val start = System.currentTimeMillis()
            val r = block()
            BoobBot.log.info("[Timer:$timerName] Took ${System.currentTimeMillis() - start}ms")
            return r
        }
    }
}