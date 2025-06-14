package bot.boobbot.entities.internals

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.BootyDropper
import bot.boobbot.entities.framework.Context
import bot.boobbot.handlers.*
import bot.boobbot.utils.Formats
//import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.internal.JDAImpl
import net.dv8tion.jda.internal.entities.UserImpl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.util.*
import java.util.concurrent.TimeUnit

class CustomShardManager(private val token: String, sm: ShardManager, shardCount: Int) : ShardManager by sm, EventListener {
    var guildCount = 0L
        private set
    var userCount = 0L
        private set

    var shardCount = shardCount
        private set

    val allShardsConnected: Boolean
        get() = this.shards.all { it.status == JDA.Status.CONNECTED || it.status == JDA.Status.LOADING_SUBSYSTEMS }

    val onlineShards: List<JDA>
        get() = this.shards.filter { it.status == JDA.Status.CONNECTED }

    var readyFired = false
        private set

    private val anonymousUser: User = UserImpl(0L, sm.shards.first() as JDAImpl)
        .setBot(false)
        .setName("Hidden User")
        .setDiscriminator(0)

    init {
        sm.addEventListener(this)

        BoobBot.scheduler.scheduleAtFixedRate({
            BoobBot.log.debug("Updating stats count.")
            guildCount = guildCache.size()
            userCount = userCache.size()
        }, 0, 5, TimeUnit.MINUTES)
    }

    override fun onEvent(event: GenericEvent) {
        if (event is ReadyEvent && allShardsConnected && !readyFired) {
            readyFired = true

            BoobBot.shardManager.setPresence(OnlineStatus.ONLINE, Activity.playing("@BoobBot help"))
            BoobBot.log.info(Formats.readyFormat)

            if (shardCount == -1) {
                shardCount = shardsTotal
            }
        }
    }

    fun authorOrAnonymous(ctx: Context): User = authorOrAnonymous(ctx.user)

    fun authorOrAnonymous(user: User): User {
        return anonymousUser.takeIf { BoobBot.database.getUserAnonymity(user.idLong) } ?: user
    }

    fun retrieveSessionInfo() = SessionInfo.from(token)

    companion object {
        fun create(token: String, shardCount: Int = -1): CustomShardManager {
            val jdaHttp = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_1_1))
                .build()

            val disabledIntents = EnumSet.of(
                // Disable moderation
                GatewayIntent.AUTO_MODERATION_CONFIGURATION,
                GatewayIntent.AUTO_MODERATION_EXECUTION,
                GatewayIntent.GUILD_MODERATION,
                // Disable typing
                GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.DIRECT_MESSAGE_TYPING,
                // Disable member and presence caching
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_PRESENCES,
                // Disable reactions
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                // Disable events for the following:
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.GUILD_MODERATION,
                GatewayIntent.GUILD_INVITES,
                GatewayIntent.GUILD_WEBHOOKS,
                GatewayIntent.SCHEDULED_EVENTS,
                // Disable Message Content
                GatewayIntent.MESSAGE_CONTENT
            )

            val disabledCacheFlags = EnumSet.of(
                CacheFlag.ACTIVITY,
                CacheFlag.CLIENT_STATUS,
                CacheFlag.EMOJI,
                CacheFlag.FORUM_TAGS,
                CacheFlag.ONLINE_STATUS,
                CacheFlag.ROLE_TAGS,
                CacheFlag.SCHEDULED_EVENTS,
                CacheFlag.STICKER
            )

            val allIntents = GatewayIntent.ALL_INTENTS
            val disabledIntentsInt = GatewayIntent.getRaw(disabledIntents)
            val enabledIntentsInt = allIntents and disabledIntentsInt.inv()
            val enabledIntents = GatewayIntent.getIntents(enabledIntentsInt)

            val sm = DefaultShardManagerBuilder.create(token, enabledIntents)
                .setShardsTotal(shardCount)
                .setActivity(Activity.playing("Booting...."))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(
                    BoobBot.waiter,
                    MessageHandler(),
                    EventHandler(),
                    UserContextHandler(),
                    SlashHandler(),
                    ComponentHandler(),
                    BootyDropper()
                )
//                .setAudioSendFactory(NativeAudioSendFactory())
                .setHttpClient(jdaHttp)
                .disableCache(disabledCacheFlags)
                .setMemberCachePolicy(MemberCachePolicy.VOICE)
                .setSessionController(CustomSessionController(16))
                .setBulkDeleteSplittingEnabled(false)

            return CustomShardManager(token, sm.build(), shardCount)
        }

        fun retrieveRemainingSessionCount(token: String) = SessionInfo.from(token)?.sessionLimitRemaining ?: 0
    }
}
