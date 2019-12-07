package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.CodeblockBuilder
import bot.boobbot.misc.Utils
import com.sun.management.OperatingSystemMXBean
import org.jetbrains.kotlin.utils.addToStdlib.sumByLong
import org.json.JSONObject
import java.lang.management.ManagementFactory
import java.text.DecimalFormat
import kotlin.math.max

@CommandProperties(description = "Overview of BoobBot's process")
class Stats : AsyncCommand {

    private val dpFormatter = DecimalFormat("0.00")

    override suspend fun executeAsync(ctx: Context) {
        val rUsedRaw = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val rPercent = dpFormatter.format(rUsedRaw.toDouble() / Runtime.getRuntime().totalMemory() * 100)
        val usedMB = dpFormatter.format(rUsedRaw.toDouble() / 1048576)

//        val servers = BoobBot.shardManager.guildCache.size()
//        val users = BoobBot.shardManager.userCache.size()
        val players = BoobBot.musicManagers.filter { it.value.player.playingTrack != null }.size

        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.onlineShards.size
        val averageShardLatency = BoobBot.shardManager.averageGatewayPing.toInt()

        val osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)
        val procCpuUsage = dpFormatter.format(osBean.processCpuLoad * 100)
        val sysCpuUsage = dpFormatter.format(osBean.systemCpuLoad * 100)

        val metrics = JSONObject(BoobBot.metrics.render().get())
        val comsUsed =
            if (metrics.has("command")) metrics.getJSONObject("command").getInt("Total Events") else 0
        val comsPerSec =
            if (metrics.has("command")) metrics.getJSONObject("command").getDouble("Events per Second (last Minute)") else 0.0

        val guildJoin =
            if (metrics.has("GuildJoin")) metrics.getJSONObject("GuildJoin").getInt("Total Events") else 0
        val guildLeave =
            if (metrics.has("GuildLeave")) metrics.getJSONObject("GuildLeave").getInt("Total Events") else 0

        val ready =
            if (metrics.has("Ready")) metrics.getJSONObject("Ready").getInt("Total Events") else 0 // can never be null

        val reconnected =
            if (metrics.has("Reconnected")) metrics.getJSONObject("Reconnected").getInt("Total Events") else 0
        val resumed =
            if (metrics.has("Resumed")) metrics.getJSONObject("Resumed").getInt("Total Events") else 0
        val disconnect =
            if (metrics.has("Disconnect")) metrics.getJSONObject("Disconnect").getInt("Total Events") else 0

        val msgSeen = metrics.getJSONObject("MessageReceived").getInt("Total Events") // can never be null
        val msgSeenPerSec = metrics.getJSONObject("MessageReceived").getDouble("Events per Second (last Minute)")

        val everyOneSeen =
            if (!metrics.isNull("atEveryoneSeen")) metrics.getJSONObject("atEveryoneSeen").getInt("Total Events") else 0

        val beans = ManagementFactory.getGarbageCollectorMXBeans()

        val totalCollections = beans.sumByLong { max(it.collectionCount, 0) }
        val totalCollectionTime = beans.sumByLong { max(it.collectionTime, 0) }
        val averageCollectionTime = if (totalCollections > 0 && totalCollectionTime > 0)
            totalCollectionTime / totalCollections
        else
            0

        val block = CodeblockBuilder("ini") {
            +"[ JVM ]"
            "Uptime              = "..Utils.fTime(System.currentTimeMillis() - BoobBot.startTime)
            "Threads             = "..Thread.activeCount()
            "JVM_CPU_Usage       = "..procCpuUsage
            "System_CPU_Usage    = "..sysCpuUsage
            "RAM_Usage           = ".."$usedMB MB ($rPercent)"
            "Total_GC_Count      = "..totalCollections
            "Total_GC_Time       = ".."${totalCollectionTime}ms"
            "Avg_GC_Cycle        = ".."${dpFormatter.format(averageCollectionTime)}ms"
            +""
            +"[ BoobBot ]"
            "Guilds              = "..BoobBot.guilds
            "Users               = "..BoobBot.users
            "Audio_Players       = "..players
            "Shards_Online       = ".."$shardsOnline/$shards"
            "Average_Latency     = ".."${averageShardLatency}ms"
            +""
            +"[ Metrics ]"
            "At_Everyone_Seen    = "..everyOneSeen
            "Commands_Used       = "..comsUsed
            "Commands_Per_second = ".."${dpFormatter.format(comsPerSec)}/sec"
            "Messages_Seen       = "..msgSeen
            "Messages_Per_second = ".."${dpFormatter.format(msgSeenPerSec)}/sec"
            "Guilds_Joined       = "..guildJoin
            "Guilds_Left         = "..guildLeave
            "Ready_Events        = "..ready
            "Resumed_Events      = "..resumed
            "Reconnected_Events  = "..reconnected
            "Disconnect_Events   = "..disconnect
        }.build()

        ctx.send(block)
    }

}