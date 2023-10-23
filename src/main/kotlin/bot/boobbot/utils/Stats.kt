package bot.boobbot.utils

import bot.boobbot.BoobBot
import bot.boobbot.entities.misc.ApiServer
import com.sun.management.OperatingSystemMXBean
import net.dv8tion.jda.api.JDA
import org.jetbrains.kotlin.utils.addToStdlib.sumByLong
import java.lang.management.ManagementFactory
import java.text.DecimalFormat
import kotlin.math.max

class Stats(
    val uptime: String,
    val threads: Int,
    val systemCpuUsage: String,
    val processCpuUsage: String,
    val ramUsedMb: String,
    val ramUsedPercent: String,
    val totalGarbageCollectionCount: Long,
    val totalGarbageCollectionTime: String,
    val averageGarbageCollectionTime: String,
    val guildCount: Int,
    val totalMemberCount: Int,
    val averageShardLatency: Int,
    val shardsTotal: Int,
    val shardsOnline: Int,
    val audioPlayers: Int
) {
    companion object {
        private val dpFormatter = DecimalFormat("0.00")

        fun get(): Stats {
            val rUsedRaw = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            val rPercent = dpFormatter.format(rUsedRaw.toDouble() / Runtime.getRuntime().totalMemory() * 100)
            val usedMB = dpFormatter.format(rUsedRaw.toDouble() / 1048576)

            val shards = BoobBot.shardManager.shardsTotal
            val shardsOnline = BoobBot.shardManager.shards.filter { it.status == JDA.Status.CONNECTED }.size
            val averageShardLatency = BoobBot.shardManager.averageGatewayPing.toInt()

            val osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)
            val procCpuUsage = dpFormatter.format(osBean.processCpuLoad * 100)
            val sysCpuUsage = dpFormatter.format(osBean.systemCpuLoad * 100)
            val players = BoobBot.musicManagers.values.filter { it.player.playingTrack != null }.size

            val beans = ManagementFactory.getGarbageCollectorMXBeans()
            val totalCollections = beans.sumByLong { max(it.collectionCount, 0) }
            val totalCollectionTime = beans.sumByLong { max(it.collectionTime, 0) }
            val averageCollectionTime = if (totalCollections > 0 && totalCollectionTime > 0) totalCollectionTime / totalCollections else 0

            return Stats(
                Utils.fTime(System.currentTimeMillis() - BoobBot.startTime),
                Thread.activeCount(),
                sysCpuUsage,
                procCpuUsage,
                usedMB,
                rPercent,
                totalCollections,
                "${totalCollectionTime}ms",
                "${dpFormatter.format(averageCollectionTime)}ms",
                BoobBot.shardManager.guilds.size,
                BoobBot.shardManager.guilds.sumOf { it.memberCount },
                averageShardLatency,
                shards,
                shardsOnline,
                players
            )
        }
    }
}
