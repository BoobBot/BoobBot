package bot.boobbot.internals

import bot.boobbot.BoobBot
import bot.boobbot.handlers.EventHandler
import bot.boobbot.handlers.MessageHandler
import bot.boobbot.misc.Utils
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.cache.CacheFlag
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.json.JSONObject
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class CustomShardManager(private val token: String, sm: ShardManager) : ShardManager by sm {
    var guildCount = 0L
        private set

    var userCount = 0L
        private set

    val allShardsConnected: Boolean
        get() = this.shards.all { it.status == JDA.Status.CONNECTED || it.status == JDA.Status.LOADING_SUBSYSTEMS }

    val onlineShards: List<JDA>
        get() = this.shards.filter { it.status == JDA.Status.CONNECTED }

    val home: Guild?
        get() = this.getGuildById(BoobBot.config.homeGuild)

    init {
        BoobBot.scheduler.scheduleAtFixedRate(::updateStats, 0, 5, TimeUnit.MINUTES)
    }

    fun updateStats() {
        BoobBot.log.debug("Updating stats count!")
        guildCount = guildCache.size()
        userCount = userCache.size()
    }

    fun retrieveRemainingSessionCount(): Int {
        return try {
            val url = URL("https://discordapp.com/api/gateway/bot")
            val connection = url.openConnection()
            connection.setRequestProperty("Authorization", "Bot $token")

            val res = Utils.readAll(connection.getInputStream())
            val json = JSONObject(res)

            json.getJSONObject("session_start_limit").getInt("remaining")
        } catch (e: Exception) {
            -1
        }
    }

    companion object {
        fun create(token: String, shardCount: Int = -1): CustomShardManager {
            val jdaHttp = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_1_1))
                .build()

            val sm = DefaultShardManagerBuilder()
                .setToken(token)
                .setShardsTotal(shardCount)
                .setActivity(Activity.playing("Booting...."))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(BoobBot.waiter, MessageHandler(), EventHandler())
                .setAudioSendFactory(NativeAudioSendFactory())
                .setHttpClient(jdaHttp)
                .setDisabledCacheFlags(EnumSet.of(CacheFlag.EMOTE, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS))
                .setSessionController(SessionController2ElectricBoogaloo())
                .build()

            return CustomShardManager(token, sm)
        }
    }

}
