package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Utils
import com.sun.management.OperatingSystemMXBean
import net.dv8tion.jda.core.JDA
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

        toSend.append("```ini\n")
                .append("[ JVM ]\n")
                .append("Uptime          = ").append(Utils.fTime(System.currentTimeMillis() - BoobBot.startTime)).append("\n")
                .append("JVM_CPU_Usage   = ").append(procCpuUsage).append("%\n")
                .append("System_CPU_Usage= ").append(sysCpuUsage).append("%\n")
                .append("RAM_Usage       = ").append(usedMB).append("MB (").append(rPercent).append("%)\n")
                .append("Threads         = ").append(Thread.activeCount()).append("\n\n")
                .append("[ BoobBot ]\n")
                .append("Guilds          = ").append(servers).append("\n")
                .append("Users           = ").append(users).append("\n")
                .append("Shards_Online   = ").append(shardsOnline).append("/").append(shards).append("\n")
                .append("Average_Latency = ").append(averageShardLatency).append("ms\n")
                .append("```")

        ctx.send(toSend.toString())
    }

}