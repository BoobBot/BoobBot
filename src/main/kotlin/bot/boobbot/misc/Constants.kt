package bot.boobbot.misc

import bot.boobbot.BoobBot
import java.util.*


class Constants {
    companion object {
        val OWNERS: List<Long> = Arrays.asList(
            248294452307689473L, 95645231248048128L, 472573259108319237L, 173237945149423619L, 180093157554388993L
        )

        var TOKEN = BoobBot.dotenv["TOKEN"] ?: ""

        val DEBUG_TOKEN = BoobBot.dotenv["DEBUG_TOKEN"] ?: ""

        const val HOME_GUILD = 440526421388165120L

        var RDY_WEBHOOK = BoobBot.dotenv["RDY_WEBHOOK"] ?: ""

        var GJLOG_WEBHOOK = BoobBot.dotenv["GJLOG_WEBHOOK"] ?: ""

        var GLLOG_WEBHOOK = BoobBot.dotenv["GLLOG_WEBHOOK"] ?: ""

        var BB_API_KEY = BoobBot.dotenv["BB_API_KEY"] ?: ""

        var BB_DB_KEY = BoobBot.dotenv["BB_API_KEY"] ?: "GAY"

        var MEMER_IMGEN_KEY = BoobBot.dotenv["MEMER_IMGEN_KEY"] ?: ""

        var SENTRY_DSN = BoobBot.dotenv["SENTRY_DSN"] ?: ""

    }
}
