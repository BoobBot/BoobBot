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

        var HOME_GUILD = BoobBot.dotenv["HOME_GUILD"] ?: ""

        var RDY_WEBHOOK = BoobBot.dotenv["RDY_WEBHOOK"] ?: ""

        var GJLOG_WEBHOOK = BoobBot.dotenv["GJLOG_WEBHOOK"] ?: ""

        var GLLOG_WEBHOOK = BoobBot.dotenv["GLLOG_WEBHOOK"] ?: ""

        var BB_API_KEY = BoobBot.dotenv["BB_API_KEY"] ?: ""

        var LBOTS_API_KEY = BoobBot.dotenv["LBOTS_API_KEY"] ?: "."

        var BB_API_URL = BoobBot.dotenv["BB_API_URL"] ?: ""

        var BB_DB_KEY = BoobBot.dotenv["BB_DB_KEY"] ?: ""

        var BB_DB_URL = BoobBot.dotenv["BB_DB_URL"] ?: ""

        var MEMER_IMGEN_KEY = BoobBot.dotenv["MEMER_IMGEN_KEY"] ?: ""

        var SENTRY_DSN = BoobBot.dotenv["SENTRY_DSN"] ?: ""

        var AUTO_PORN_TIME = BoobBot.dotenv["AUTO_PORN_TIME"] ?: ""


    }
}
