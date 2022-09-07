package bot.boobbot.entities.internals

import io.sentry.Sentry
import io.sentry.SentryClient
import io.sentry.event.Event
import io.sentry.event.helper.ShouldSendEventCallback
import io.sentry.event.interfaces.ExceptionInterface
import io.sentry.event.interfaces.SentryException
import org.jetbrains.kotlin.utils.addToStdlib.cast
import org.slf4j.LoggerFactory

class CustomSentryClient(sentryClient: SentryClient) : ShouldSendEventCallback {

    init {
        sentryClient.addShouldSendEventCallback(this)
    }

    private val ignoredExceptions = mutableSetOf<String>()

    fun ignore(vararg throwable: Class<out Throwable>) {
        throwable
            .map { it.simpleName }
            .map(ignoredExceptions::add)
    }

    private fun getLastRecordedException(event: Event): SentryException? {
        return event.sentryInterfaces.values.firstOrNull { it is ExceptionInterface }
            ?.cast<ExceptionInterface>()
            ?.exceptions
            ?.first
    }

    override fun shouldSend(event: Event): Boolean {
        val se = getLastRecordedException(event)
            ?: return true

//        if (se.exceptionMessage != event.message) {
//            return true
//        }

        return !ignoredExceptions.contains(se.exceptionClassName)
    }

    companion object {
        fun create(dsn: String): CustomSentryClient {
            val sentryClient = Sentry.init(dsn)
            return CustomSentryClient(sentryClient)
        }

        private val log = LoggerFactory.getLogger(CustomSentryClient::class.java)
    }

}