package bot.boobbot.misc

import bot.boobbot.BoobBot
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject


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


        fun deleteGuild(guild_id: String) {
            BoobBot.requestUtil
                .delete(
                    "http://localhost:5000/api/guilds/$guild_id",
                    createHeaders(Pair("Authorization", "GAY"))
                ).block()!!.close()
        }


        private fun getGuilds(): JSONArray {
            val guilds = BoobBot.requestUtil
                .get(
                    "http://localhost:5000/api/guilds",
                    createHeaders(Pair("Authorization", "GAY"))
                ).block()?.json()
            return guilds!!.getJSONArray("guilds")
        }


        fun autoPorn() {
            if (BoobBot.isReady) {
                BoobBot.log.info("Running auto-porn")
                val guilds: JSONArray = getGuilds()
                guilds.forEach { it ->
                    (it as JSONObject)
                    val guild = BoobBot.shardManager.getGuildById(it.getString("guild_id"))
                    if (guild == null) {
                        deleteGuild(it.getString("guild_id"))
                        return@forEach
                    }
                    val channel = guild.getTextChannelById(it.getString("channel"))
                    if (channel == null && guild.isAvailable) {
                        deleteGuild(it.getString("guild_id"))
                        return@forEach
                    }
                    //TODO get image
                    channel.sendMessage("k").queue()

                }
            }
        }


        fun auto(): Runnable = Runnable { autoPorn() }

    }
}