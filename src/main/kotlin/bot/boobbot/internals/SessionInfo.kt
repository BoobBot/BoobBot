package bot.boobbot.internals

import bot.boobbot.misc.Utils
import org.json.JSONObject

class SessionInfo(
    val recommendedShards: Int,
    val sessionLimitTotal: Int,
    val sessionLimitRemaining: Int,
    val sessionResetAfter: String
) {
    companion object {
        fun from(json: JSONObject): SessionInfo {
            val recommendedShards = json.getInt("shards")
            val session = json.getJSONObject("session_start_limit")
            val sessionLimitTotal = session.getInt("total")
            val sessionLimitRemaining = session.getInt("remaining")
            val sessionResetAfter = session.getLong("reset_after")
            val sessionResetFormatted = Utils.fTime(sessionResetAfter)

            return SessionInfo(recommendedShards, sessionLimitTotal, sessionLimitRemaining, sessionResetFormatted)
        }
    }
}
