package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Utils
import com.sun.management.OperatingSystemMXBean
import net.dv8tion.jda.core.JDA
import org.json.JSONObject
import java.lang.management.ManagementFactory
import java.text.DecimalFormat

@CommandProperties(description = "Overview of BoobBot's process")
class Stats : Command {

    private val dpFormatter = DecimalFormat("0.00")

    override fun execute(ctx: Context) {
        val toSend = StringBuilder()
        val rUsedRaw = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val rPercent = dpFormatter.format(rUsedRaw.toDouble() / Runtime.getRuntime().totalMemory() * 100)
        val usedMB = dpFormatter.format(rUsedRaw.toDouble() / 1048576)

        val servers = BoobBot.shardManager.guildCache.size()
        val users = BoobBot.shardManager.userCache.size()

        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.shards.asSequence().filter { s -> s.status == JDA.Status.CONNECTED }.count()
        val averageShardLatency = BoobBot.shardManager.averagePing.toInt()

        val osBean: OperatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)
        val procCpuUsage = dpFormatter.format(osBean.processCpuLoad * 100)
        val sysCpuUsage = dpFormatter.format(osBean.systemCpuLoad * 100)

        val players = BoobBot.musicManagers.filter { it.value.player.playingTrack != null }.count()

        val metrics = JSONObject(BoobBot.metrics.render().get())
        val comsUsed = if (metrics.has("command")) metrics.getJSONObject("command").getString("Total Events").toInt() else 0
        val comsPerSec = if (metrics.has("command")) metrics.getJSONObject("command").getString("Events per Second (last Minute)").toDouble() else 0.0

        val guildJoin = if (metrics.has("GuildJoin")) metrics.getJSONObject("GuildJoin").getString("Total Events").toInt() else 0
        val guildLeave = if (metrics.has("GuildLeave")) metrics.getJSONObject("GuildLeave").getString("Total Events").toInt() else 0

        val ready = if (metrics.has("Ready")) metrics.getJSONObject("Ready").getString("Total Events").toInt() else 0 // can never be null

        val reconnected = if (metrics.has("Reconnected")) metrics.getJSONObject("Reconnected").getString("Total Events").toInt() else 0
        val resumed = if (metrics.has("Resumed")) metrics.getJSONObject("Resumed").getString("Total Events").toInt() else 0
        val disconnect = if (metrics.has("Disconnect")) metrics.getJSONObject("Disconnect").getString("Total Events").toInt() else 0

        val msgSeen = metrics.getJSONObject("MessageReceived").getString("Total Events").toInt() // can never be null
        val msgSeenPerSec = metrics.getJSONObject("MessageReceived").getString("Events per Second (last Minute)").toDouble() // can never be null

        val everyOneSeen = if (!metrics.isNull("atEveryoneSeen")) metrics.getJSONObject("atEveryoneSeen").getString("Total Events").toInt() else 0
        //TODO add all metrics
        toSend.append("```ini\n")
                .append("[ JVM ]\n")
                .append("Uptime              = ").append(Utils.fTime(System.currentTimeMillis() - BoobBot.startTime)).append("\n")
                .append("JVM_CPU_Usage       = ").append(procCpuUsage).append("%\n")
                .append("System_CPU_Usage    = ").append(sysCpuUsage).append("%\n")
                .append("RAM_Usage           = ").append(usedMB).append("MB (").append(rPercent).append("%)\n")
                .append("Threads             = ").append(Thread.activeCount()).append("\n\n")
                .append("[ BoobBot ]\n")
                .append("Guilds              = ").append(servers).append("\n")
                .append("Users               = ").append(users).append("\n")
                .append("Audio_Players       = ").append(players).append("\n")
                .append("Shards_Online       = ").append(shardsOnline).append("/").append(shards).append("\n")
                .append("Average_Latency     = ").append(averageShardLatency).append("ms\n\n")
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