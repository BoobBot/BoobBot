package bot.boobbot.internals

import bot.boobbot.BoobBot
import bot.boobbot.flight.Context
import bot.boobbot.handlers.EconomyHandler
import bot.boobbot.handlers.EventHandler
import bot.boobbot.handlers.MessageHandler
import bot.boobbot.misc.Utils
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.internal.JDAImpl
import net.dv8tion.jda.internal.entities.UserImpl
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

    val anonymousUser = UserImpl(0L, sm.shards.first() as JDAImpl)
        .setAvatarId(null)
        .setBot(false)
        .setFake(false)
        .setName("Hidden User")
        .setDiscriminator("0000")

    init {
        BoobBot.scheduler.scheduleAtFixedRate(::updateStats, 0, 5, TimeUnit.MINUTES)
    }

    fun updateStats() {
        BoobBot.log.debug("Updating stats count!")
        guildCount = guildCache.size()
        userCount = userCache.size()
    }

    fun authorOrAnonymous(ctx: Context): User {
        return if (BoobBot.database.getUserAnonymity(ctx.author.id)) {
            BoobBot.shardManager.anonymousUser
        } else {
            ctx.author
        }
    }

    companion object {
        fun create(token: String, shardCount: Int = -1, noCache: Boolean = false): CustomShardManager {
            val jdaHttp = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_1_1))
                .build()

            val sm = DefaultShardManagerBuilder()
                .setToken(token)
                .setShardsTotal(shardCount)
                .setActivity(Activity.playing("Booting...."))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(BoobBot.waiter, MessageHandler(), EventHandler(), EconomyHandler())
                .setAudioSendFactory(NativeAudioSendFactory())
                .setHttpClient(jdaHttp)
                .disableCache(EnumSet.of(CacheFlag.EMOTE, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS))
                .setDisabledIntents(
                    GatewayIntent.GUILD_MESSAGE_TYPING,
                    GatewayIntent.DIRECT_MESSAGE_TYPING,
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_PRESENCES,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS,
                    GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                    GatewayIntent.GUILD_EMOJIS
                )
                .setSessionController(SessionController())

            if (noCache) {
                sm.setMemberCachePolicy(MemberCachePolicy.VOICE)
            }

            return CustomShardManager(token, sm.build())
        }

        fun retrieveRemainingSessionCount(token: String): Int {
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
    }

}
