package bot.boobbot.entities.internals

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.boolex.EventEvaluatorBase

class LoggingFilter : EventEvaluatorBase<ILoggingEvent>() {
    override fun evaluate(event: ILoggingEvent): Boolean {
        return event.message.contains("Event")
    }
}
