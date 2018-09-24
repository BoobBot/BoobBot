package bot.boobbot.misc

import java.util.*
import io.github.cdimascio.dotenv.dotenv


class Constants {
    companion object {
        val dotenv = dotenv()

        val OWNERS: List<Long> = Arrays.asList(
                248294452307689473L, 95645231248048128L, 472573259108319237L, 173237945149423619L
        )

        var TOKEN =  dotenv["MY_ENV_VAR1TOKEN"] ?: ""

        val DEBUG_TOKEN =  dotenv["DEBUG_TOKEN"] ?: ""

        const val HOME_GUILD = 440526421388165120L

        var RDY_WEBHOOK = dotenv["RDY_WEBHOOK"] ?: ""

        var GJLOG_WEBHOOK = dotenv["GJLOG_WEBHOOK"] ?: ""

        var GLLOG_WEBHOOK = dotenv["GLLOG_WEBHOOK"] ?: ""

        var BB_API_KEY = dotenv["BB_API_KEY"] ?: ""

        var MEMER_IMGEN_KEY = dotenv["MEMER_IMGEN_KEY"] ?: ""

        var SENTRY_DSN = dotenv["SENTRY_DSN"] ?: ""
    }
}
