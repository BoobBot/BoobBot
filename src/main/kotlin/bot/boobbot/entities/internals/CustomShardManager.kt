package bot.boobbot.entities.internals

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.handlers.EventHandler
import bot.boobbot.handlers.MessageHandler
import bot.boobbot.handlers.SlashHandler
import bot.boobbot.handlers.UserContextHandler
import bot.boobbot.utils.Formats
import bot.boobbot.utils.WebhookManager
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
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

class CustomShardManager(private val token: String, sm: ShardManager) : ShardManager by sm, EventListener {
    var guildCount = 0L
        private set
    var userCount = 0L
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
        .setDiscriminator("0000")

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
            BoobBot.shardManager.setPresence(OnlineStatus.ONLINE, Activity.playing("discord.gg/bra || @BoobBot help"))
            BoobBot.log.info(Formats.readyFormat)
            WebhookManager.sendShard(null) {
                setTitle("ALL SHARDS CONNECTED", BoobBot.inviteUrl)
                setDescription("Average Shard Ping: ${BoobBot.shardManager.averageGatewayPing}ms")
                setThumbnail(event.jda.selfUser.effectiveAvatarUrl)
                addField("Ready Info", "```\n${Formats.readyFormat}```", false)
            }
        }
    }

    fun authorOrAnonymous(ctx: Context): User {
        return anonymousUser.takeIf { BoobBot.database.getUserAnonymity(ctx.user.id) } ?: ctx.user
    }

    fun retrieveSessionInfo() = SessionInfo.from(token)

    companion object {
        fun create(token: String, shardCount: Int = -1): CustomShardManager {
            val jdaHttp = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_1_1))
                .build()

            val disabledIntents = EnumSet.of(
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
                CacheFlag.ONLINE_STATUS,
                CacheFlag.ROLE_TAGS,
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
                    SlashHandler()
                )
                .setAudioSendFactory(NativeAudioSendFactory())
                .setHttpClient(jdaHttp)
                .disableCache(disabledCacheFlags)
                .setMemberCachePolicy(MemberCachePolicy.VOICE)
                .setSessionController(CustomSessionController(16))
                .setBulkDeleteSplittingEnabled(false)

            return CustomShardManager(token, sm.build())
        }

        fun retrieveRemainingSessionCount(token: String) = SessionInfo.from(token)?.sessionLimitRemaining ?: 0
    }
}
