package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
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
        //TODO move all this to a func
        val toSend = StringBuilder()
        val rUsedRaw = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val rPercent = dpFormatter.format(rUsedRaw.toDouble() / Runtime.getRuntime().totalMemory() * 100)
        val usedMB = dpFormatter.format(rUsedRaw.toDouble() / 1048576)

        val servers = BoobBot.shardManager.guildCache.size()
        val users = BoobBot.shardManager.userCache.size()
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

        val beans = Utils.timed("beanCollection") {
            ManagementFactory.getGarbageCollectorMXBeans()
        }

        val totalCollections = beans.sumByLong { max(it.collectionCount, 0) }
        val totalCollectionTime = beans.sumByLong { max(it.collectionTime, 0) }
        val averageCollectionTime = if (totalCollections > 0 && totalCollectionTime > 0)
            totalCollectionTime / totalCollections
        else
            0

        toSend.append("```ini\n")
            .append("[ JVM ]\n")
            .append("Uptime              = ").append(Utils.fTime(System.currentTimeMillis() - BoobBot.startTime))
            .append("\n")
            .append("Threads             = ").append(Thread.activeCount()).append("\n")
            .append("JVM_CPU_Usage       = ").append(procCpuUsage).append("%\n")
            .append("System_CPU_Usage    = ").append(sysCpuUsage).append("%\n")
            .append("RAM_Usage           = ").append(usedMB).append("MB (").append(rPercent).append("%)\n")
            .append("Total_GC_Count      = ").append(totalCollections).append("\n")
            .append("Total_GC_Time       = ").append(totalCollectionTime).append("ms").append("\n")
            .append("Avg_GC_Cycle        = ").append(dpFormatter.format(averageCollectionTime)).append("ms")
            .append("\n\n")
            .append("[ BoobBot ]\n")
            .append("Guilds              = ").append(servers).append("\n")
            .append("Users               = ").append(users).append("\n")
            .append("Audio_Players       = ").append(players).append("\n")
            .append("Shards_Online       = ").append(shardsOnline).append("/").append(shards)
            .append("\n") // shardsOnline
            .append("Average_Latency     = ").append(averageShardLatency).append("ms\n\n") // averageShardLatency
            .append("[ Metrics_Since_Boot ]\n")
            .append("At_Everyone_Seen    = ").append(everyOneSeen).append("\n")
            .append("Commands_Used       = ").append(comsUsed).append("\n")
            .append("Commands_Per_second = ").append(dpFormatter.format(comsPerSec)).append("/sec").append("\n")
            .append("Messages_Seen       = ").append(msgSeen).append("\n")
            .append("Messages_Per_second = ").append(dpFormatter.format(msgSeenPerSec)).append("/sec").append("\n")
            .append("Guilds_Joined       = ").append(guildJoin).append("\n")
            .append("Guilds_Left         = ").append(guildLeave).append("\n")
            .append("Ready_Events        = ").append(ready).append("\n")
            .append("Resumed_Events      = ").append(resumed).append("\n")
            .append("Reconnected_Events  = ").append(reconnected).append("\n")
            .append("Disconnect_Events   = ").append(disconnect).append("\n")
            .append("```")

        ctx.send(toSend.toString())
    }

}