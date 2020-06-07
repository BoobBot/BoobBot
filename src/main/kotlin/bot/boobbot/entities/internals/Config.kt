package bot.boobbot.entities.internals

import bot.boobbot.utils.Klash
import io.github.cdimascio.dotenv.dotenv
import java.nio.file.Paths

class Config(
    val TOKEN: String,
    val DEBUG_TOKEN: String,
    val SHARD_COUNT: Int = 1,
    val RDY_WEBHOOK: String,
    val GJLOG_WEBHOOK: String,
    val GLLOG_WEBHOOK: String,
    val BB_API_KEY: String,
    val BB_DB_KEY: String,
    val BB_DB_URL: String,
    val MEMER_IMGEN_KEY: String,
    val SENTRY_DSN: String,
    val PATREON_KEY: String,
    val MONGO_DB_URL: String,
    val DISCORD_TOKEN_URL: String = "https://discordapp.com/api/oauth2/token",
    val DISCORD_AUTH_URL: String = "https://discordapp.com/api/oauth2/authorize",
    val DISCORD_REVOKE_URL: String = "https://discordapp.com/api/oauth2/token/revoke",
    val DISCORD_CLIENT_SECRET: String,
    val OAUTH_REDIRECT_URL: String = "http://localhost:8769/oauth",
    val SESSION_KEY: String
) {
    companion object {
        val OWNERS = listOf(
            248294452307689473L, 180093157554388993L
        )

        fun load(path: String = Paths.get("").toAbsolutePath().toString()): Config {
            val dotenv = dotenv {
                directory = path
                filename = "bb.env"
                ignoreIfMalformed = true
                ignoreIfMissing = false
            }

            return Klash.construct<Config>(
                { dotenv[it] },
                { "" })
        }
    }

}
