package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.misc.Utils
import com.sun.management.OperatingSystemMXBean
import de.mxro.metrics.jre.Metrics
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.http.ContentType
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.dv8tion.jda.core.JDA
import org.json.JSONArray
import org.json.JSONObject
import java.lang.management.ManagementFactory
import java.text.DecimalFormat

class ApiHandler {

    fun startServer(){
        // api for new site

        embeddedServer(Netty, 8888) {
            install(CORS)
            routing {

                get("/") {
                    BoobBot.metrics.record(Metrics.happened("request /"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    call.respondRedirect("https://boob.bot", true)
                }

                get("/stats") {
                    BoobBot.metrics.record(Metrics.happened("request /stats"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    val dpFormatter = DecimalFormat("0.00")
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
                    val players = BoobBot.musicManagers.filter { p -> p.value.player.playingTrack != null }.count()

                    var totalGarbageCollections = 0L
                    var garbageCollectionTime = 0L
                    ManagementFactory.getGarbageCollectorMXBeans().forEach { gc ->
                        val count = gc.collectionCount
                        if(count >= 0) {
                            totalGarbageCollections += count
                        }
                        val time = gc.collectionTime
                        if(time >= 0) {
                            garbageCollectionTime += time
                        } }

                    val jvm = JSONObject()
                            .put("Uptime", Utils.fTime(System.currentTimeMillis() - BoobBot.startTime))
                            .put("JVM_CPU_Usage", procCpuUsage)
                            .put("System_CPU_Usage", sysCpuUsage)
                            .put("RAM_Usage", "${usedMB}MB($rPercent%)")
                            .put("Threads", Thread.activeCount())
                            .put("Total_GC_Count", totalGarbageCollections)
                            .put("Total_GC_Time", "${garbageCollectionTime}ms")

                    val bb = JSONObject()
                            .put("Guilds", servers)
                            .put("Users", users)
                            .put("Audio_Players", players)
                            .put("Shards_Online", "$shardsOnline/$shards")
                            .put("Average_Latenc", "${averageShardLatency}ms")

                    call.respondText("{\"stats\": ${JSONObject().put("bb", bb).put("jvm", jvm)}}", ContentType.Application.Json)

                }

                get("/metrics") {
                    BoobBot.metrics.record(Metrics.happened("request /metrics"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    call.respondText("{\"metrics\": ${BoobBot.metrics.render().get()}}", ContentType.Application.Json)
                }

                get("/health") {
                    BoobBot.metrics.record(Metrics.happened("request /health"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    call.respondText("{\"health\": \"ok\", \"ping\": ${BoobBot.shardManager.averagePing}}", ContentType.Application.Json)
                }

                get("/pings") {
                    BoobBot.metrics.record(Metrics.happened("request /pings"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    val pings = JSONArray()
                    for (e in BoobBot.shardManager.statuses.entries) pings.put(JSONObject().put("shard", e.key.shardInfo.shardId).put("ping", e.key.ping).put("status", e.value))
                    call.respondText("{\"status\": $pings}", ContentType.Application.Json)
                }

                get("/commands") {
                    BoobBot.metrics.record(Metrics.happened("request /commands"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    val response = JSONObject()
                    BoobBot.commands.values.filter { command -> command.properties.category.name != "DEV" }.forEach { command ->
                        if(!response.has(command.properties.category.name)){
                            response.put(command.properties.category.name, JSONArray())
                        }
                        val array = response.getJSONArray(command.properties.category.name)
                        array.put(JSONObject()
                                .put("command", command.name)
                                .put("category", command.properties.category)
                                .put("description", command.properties.description)
                                .put("aliases", "[${command.properties.aliases.joinToString(", ")}]"))

                    }
                    call.respondText("{\"commands\": $response}", ContentType.Application.Json)
                }

            }
        }.start(wait = false)
    }
}