package bot.boobbot.misc

import java.util.*

class Constants {
    companion object {
        val OWNERS: List<Long> = Arrays.asList(
                248294452307689473L, 95645231248048128L, 472573259108319237L, 173237945149423619L
        )

        var TOKEN = System.getenv("TOKEN") ?: ""

        val DEBUG_TOKEN = System.getenv("DEBUG_TOKEN") ?: ""

        const val HOME_GUILD = 440526421388165120L

        var RDY_WEBHOOK = System.getenv("RDY_WEBHOOK") ?: "B"

        var GJLOG_WEBHOOK = System.getenv("GJLOG_WEBHOOK") ?: ""

        var GLLOG_WEBHOOK = System.getenv("GLLOG_WEBHOOK") ?: ""

        var BB_API_KEY = System.getenv("BB_API_KEY") ?: ""

        var MEMER_IMGEN_KEY = System.getenv("MEMER_IMGEN_KEY") ?: ""

        var SENTRY_DSN = System.getenv("SENTRY_DSN") ?: ""
    }
}
