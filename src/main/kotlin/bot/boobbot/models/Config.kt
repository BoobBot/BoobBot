package bot.boobbot.models

import bot.boobbot.misc.get
import io.github.cdimascio.dotenv.dotenv
import java.nio.file.Paths

class Config(
    val token: String,
    val debugToken: String,
    val shardCount: Int,
    val homeGuild: String,
    val readyWebhook: String,
    val gjWebhook: String,
    val glWebhook: String,
    val bbApiKey: String,
    val lbotsApiKey: String,
    val bbApiUrl: String,
    val bbDbKey: String,
    val bbDbUrl: String,
    val memerImgenKey: String,
    val sentryDsn: String,
    val patreonApiKey: String,
    val mongoDbUrl: String,
    val shardIdentifyDelay: Long
) {

    companion object {
        val owners = listOf(
            248294452307689473L, 180093157554388993L
        )

        fun load(path: String = Paths.get("").toAbsolutePath().toString()): Config {
            val dotenv = dotenv {
                directory = path
                filename = "bb.env"
                ignoreIfMalformed = true
                ignoreIfMissing = false
            }

            return Config(
                dotenv.get("TOKEN", ""),
                dotenv.get("DEBUG_TOKEN", ""),
                dotenv.get("SHARD_COUNT", "-1").toInt(),
                dotenv.get("HOME_GUILD", ""),
                dotenv.get("RDY_WEBHOOK", ""),
                dotenv.get("GJLOG_WEBHOOK", ""),
                dotenv.get("GLLOG_WEBHOOK", ""),
                dotenv.get("BB_API_KEY", ""),
                dotenv.get("LBOTS_API_KEY", ""),
                dotenv.get("BB_API_URL", ""),
                dotenv.get("BB_DB_KEY", ""),
                dotenv.get("BB_DB_URL", ""),
                dotenv.get("MEMER_IMGEN_KEY", ""),
                dotenv.get("SENTRY_DSN", ""),
                dotenv.get("PATREON_KEY", ""),
                dotenv.get("MONGO_DB_URL", ""),
                dotenv.get("SHARD_IDENTIFY_DELAY", "5000").toLong()
            )
        }
    }

}
