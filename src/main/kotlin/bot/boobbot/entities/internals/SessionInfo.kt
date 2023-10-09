package bot.boobbot.entities.internals

import bot.boobbot.utils.Utils
import org.json.JSONObject
import java.net.URL

class SessionInfo(
    val recommendedShards: Int,
    val sessionLimitTotal: Int,
    val sessionLimitRemaining: Int,
    val sessionResetAfter: String,
    val maxConcurrency: Int
) {
    companion object {
        fun from(token: String): SessionInfo? {
            return try {
                val url = URL("https://discordapp.com/api/gateway/bot")
                val connection = url.openConnection()
                connection.setRequestProperty("Authorization", "Bot $token")

                val res = Utils.readAll(connection.getInputStream())
                val json = JSONObject(res)

                from(json)
            } catch (e: Exception) {
                null
            }
        }

        fun from(json: JSONObject): SessionInfo {
            val recommendedShards = json.getInt("shards")
            val session = json.getJSONObject("session_start_limit")
            val sessionLimitTotal = session.getInt("total")
            val sessionLimitRemaining = session.getInt("remaining")
            val sessionResetAfter = session.getLong("reset_after")
            val sessionResetFormatted = Utils.fTime(sessionResetAfter)
            val maxConcurrency = session.getInt("max_concurrency")

            return SessionInfo(
                recommendedShards,
                sessionLimitTotal,
                sessionLimitRemaining,
                sessionResetFormatted,
                maxConcurrency
            )
        }
    }
}
