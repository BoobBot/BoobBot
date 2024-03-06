package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.internals.CodeblockBuilder
import bot.boobbot.utils.Stats
import org.json.JSONObject
import java.text.DecimalFormat

@CommandProperties(description = "Overview of BoobBot's process", groupByCategory = true)
class Stats : Command {

    private val dpFormatter = DecimalFormat("0.00")

    override fun execute(ctx: Context) = full(ctx)

    @SubCommand(description = "Overview of bot statistics.")
    fun minimal(ctx: Context) {
        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.onlineShards.size
        val averageShardLatency = BoobBot.shardManager.averageGatewayPing
        ctx.reply("**Shard info**: $shardsOnline/$shards\n**Average latency**: ${averageShardLatency}ms")
    }

    @SubCommand(description = "Full bot statistics.")
    fun full(ctx: Context) {
        val stats = Stats.get()

        val metrics = JSONObject(BoobBot.metrics.render().get())
        val comsUsed = if (metrics.has("command")) metrics.getJSONObject("command").getInt("Total Events") else 0
        val comsPerSec = if (metrics.has("command")) metrics.getJSONObject("command").getDouble("Events per Second (last Minute)") else 0.0

        val guildJoin = if (metrics.has("GuildJoin")) metrics.getJSONObject("GuildJoin").getInt("Total Events") else 0
        val guildLeave = if (metrics.has("GuildLeave")) metrics.getJSONObject("GuildLeave").getInt("Total Events") else 0

        val ready = if (metrics.has("Ready")) metrics.getJSONObject("Ready").getInt("Total Events") else 0 // can never be null

        val reconnected = if (metrics.has("Reconnected")) metrics.getJSONObject("Reconnected").getInt("Total Events") else 0
        val resumed = if (metrics.has("Resumed")) metrics.getJSONObject("Resumed").getInt("Total Events") else 0
        val disconnect = if (metrics.has("Disconnect")) metrics.getJSONObject("Disconnect").getInt("Total Events") else 0

        val msgSeen = metrics.getJSONObject("MessageReceived").getInt("Total Events") // can never be null
        val msgSeenPerSec = metrics.getJSONObject("MessageReceived").getDouble("Events per Second (last Minute)")

        val everyOneSeen = if (!metrics.isNull("atEveryoneSeen")) metrics.getJSONObject("atEveryoneSeen").getInt("Total Events") else 0

        val block = CodeblockBuilder("ini") {
            +"[ JVM ]"
            "Uptime              = "..stats.uptime
            "Threads             = "..stats.threads
            "JVM_CPU_Usage       = "..stats.processCpuUsage
            "System_CPU_Usage    = "..stats.systemCpuUsage
            "RAM_Usage           = ".."${stats.ramUsedMb} MB (${stats.ramUsedPercent}%)"
            "Total_GC_Count      = "..stats.totalGarbageCollectionCount
            "Total_GC_Time       = "..stats.totalGarbageCollectionTime
            "Avg_GC_Cycle        = "..stats.averageGarbageCollectionTime
            +""
            +"[ BoobBot ]"
            "Guilds              = "..stats.guildCount
            "Users               = "..stats.totalMemberCount
            "Audio_Players       = "..stats.audioPlayers
            "Shards_Online       = ".."${stats.shardsOnline}/${stats.shardsTotal}"
            "Average_Latency     = ".."${stats.averageShardLatency}ms"
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

        ctx.reply(block)
    }

}