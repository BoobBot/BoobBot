package bot.boobbot.misc

import bot.boobbot.BoobBot
import bot.boobbot.flight.Context
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.nio.channels.spi.AbstractInterruptibleChannel


class AutoPorn {
    companion object {

        suspend fun checkExists(guild_id: String): Boolean {
            val check = BoobBot.requestUtil
                .get(
                    "http://localhost:5000/api/guilds/$guild_id",
                    createHeaders(Pair("Authorization", "GAY"))
                ).await()
                ?: return false
            if (check.code() == 404) {
                check.close()
                return false
            }
            check.close()
            return true
        }


        suspend fun createGuild(guild_id: String, channel_id: String, type: String): Boolean {

            val JSON = MediaType.parse("application/json; charset=utf-8")
            val Body = RequestBody.create(
                JSON,
                "{\"guild_id\": \"$guild_id\", \"channel\": \"$channel_id\", \"type\": \"$type\"}"
            )
            val res = BoobBot.requestUtil
                .post(
                    "http://localhost:5000/api/guilds", Body,
                    createHeaders(Pair("Authorization", "GAY"))
                )
                .await() ?: return false
            if (res.code() == 201) {
                return true
            }
            return false
        }


        suspend fun deleteGuild(guild_id: String) {
            BoobBot.requestUtil
                .delete(
                    "http://localhost:5000/api/guilds/$guild_id",
                    createHeaders(Pair("Authorization", "GAY"))
                ).await()
        }


        fun getGuilds() {
            val k = BoobBot.requestUtil
                .get(
                    "http://localhost:5000/api/guilds",
                    createHeaders(Pair("Authorization", "GAY"))
                ).block()?.json() ?: return
            BoobBot.log.info(k.toString(3))
            val d: JSONArray = k.getJSONArray("guilds")
            d.forEach { it ->
                BoobBot.log.info((it as JSONObject).get("channel").toString())
            }
        }


        fun autoPorn() {
            if (BoobBot.isReady) {
                BoobBot.log.info("Running autoporn")
            }
        }

        fun auto(): Runnable = Runnable { autoPorn() }

    }
}