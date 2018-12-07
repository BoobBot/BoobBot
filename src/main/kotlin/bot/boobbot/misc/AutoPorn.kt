package bot.boobbot.misc

import bot.boobbot.BoobBot
import net.dv8tion.jda.core.EmbedBuilder
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant


class AutoPorn {
    companion object {
        private var dbHeaders = createHeaders(Pair("Authorization", Constants.BB_DB_KEY))
        suspend fun checkExists(guild_id: String): Boolean {
            val check = BoobBot.requestUtil
                .get(
                    "https://db.boob.bot/api/guilds/$guild_id",
                    dbHeaders
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
                    "https://db.boob.bot/api/guilds", Body,
                    dbHeaders
                )
                .await() ?: return false
            if (res.code() == 201) {
                return true
            }
            return false
        }


        suspend fun getStatus(guild_id: String): String {
            val req = BoobBot.requestUtil
                .get(
                    "https://db.boob.bot/api/guilds/$guild_id",
                    dbHeaders
                ).await()
            return req!!.json()!!.getJSONObject("guild").get("channel").toString()
        }


        fun deleteGuild(guild_id: String) {
            BoobBot.requestUtil
                .delete(
                    "https://db.boob.bot/api/guilds/$guild_id",
                    dbHeaders
                ).block()!!.close()
        }


        private fun getGuilds(): JSONArray? {
            val guilds = BoobBot.requestUtil
                .get(
                    "https://db.boob.bot/api/guilds",
                    dbHeaders
                ).block()?.json()
            return guilds?.getJSONArray("guilds")
        }


        private fun autoPorn() {
            if (BoobBot.isReady) {
                BoobBot.log.info("Running auto-porn")
                val guilds: JSONArray = getGuilds() ?: return
                guilds.forEach { it ->
                    try {
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

                        var type = it.getString("type")
                        if (type == "gif") {
                            type = "Gifs"
                        }

                        val headers = createHeaders(
                            Pair("Key", Constants.BB_API_KEY)
                        )

                        val res =
                            BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$type", headers).block()?.json()
                                ?: return@forEach

                        if (!channel.canTalk()) {
                            deleteGuild(it.getString("guild_id"))
                            return@forEach
                        }
                        channel.sendMessage(
                            EmbedBuilder().apply {
                                setDescription(Formats.LEWD_EMOTE)
                                setColor(Colors.rndColor)
                                setImage(res.getString("url"))
                                setTimestamp(Instant.now())
                            }.build()
                        ).queue()

                    } catch (e: Exception) {
                        return@forEach
                    }
                }
            }
        }

        fun auto(): Runnable = Runnable { autoPorn() }

    }
}