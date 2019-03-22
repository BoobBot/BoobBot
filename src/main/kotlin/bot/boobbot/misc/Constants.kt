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

        val SHARD_COUNT: Int
            get() = BoobBot.dotenv["SHARD_COUNT"]?.toInt() ?: -1

        val HOME_GUILD = BoobBot.dotenv["HOME_GUILD"] ?: ""

        val RDY_WEBHOOK = BoobBot.dotenv["RDY_WEBHOOK"] ?: ""

        val GJLOG_WEBHOOK = BoobBot.dotenv["GJLOG_WEBHOOK"] ?: ""

        val GLLOG_WEBHOOK = BoobBot.dotenv["GLLOG_WEBHOOK"] ?: ""

        val BB_API_KEY = BoobBot.dotenv["BB_API_KEY"] ?: ""

        val LBOTS_API_KEY = BoobBot.dotenv["LBOTS_API_KEY"] ?: "."

        val BB_API_URL = BoobBot.dotenv["BB_API_URL"] ?: ""

        val BB_DB_KEY = BoobBot.dotenv["BB_DB_KEY"] ?: ""

        val BB_DB_URL = BoobBot.dotenv["BB_DB_URL"] ?: ""

        val MEMER_IMGEN_KEY = BoobBot.dotenv["MEMER_IMGEN_KEY"] ?: ""

        val SENTRY_DSN = BoobBot.dotenv["SENTRY_DSN"] ?: ""

        val AUTO_PORN_TIME = BoobBot.dotenv["AUTO_PORN_TIME"] ?: ""

    }
}
