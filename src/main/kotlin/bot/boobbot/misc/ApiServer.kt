package bot.boobbot.misc

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import com.sun.management.OperatingSystemMXBean
import de.mxro.metrics.jre.Metrics
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.request.path
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.AttributeKey
import net.dv8tion.jda.api.JDA
import org.jetbrains.kotlin.utils.addToStdlib.sumByLong
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.event.Level
import java.lang.management.ManagementFactory
import java.text.DecimalFormat
import kotlin.math.max


class ApiServer {

    private fun getStats(): JSONObject {
        val dpFormatter = DecimalFormat("0.00")
        val rUsedRaw = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val rPercent = dpFormatter.format(rUsedRaw.toDouble() / Runtime.getRuntime().totalMemory() * 100)
        val usedMB = dpFormatter.format(rUsedRaw.toDouble() / 1048576)

        val servers = BoobBot.shardManager.guildCache.size()
        val users = BoobBot.shardManager.userCache.size()

        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.shards.filter { it.status == JDA.Status.CONNECTED }.size
        val averageShardLatency = BoobBot.shardManager.averageGatewayPing.toInt()

        val osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)
        val procCpuUsage = dpFormatter.format(osBean.processCpuLoad * 100)
        val sysCpuUsage = dpFormatter.format(osBean.systemCpuLoad * 100)
        val players = BoobBot.musicManagers.filter { p -> p.value.player.playingTrack != null }.size

        val beans = Utils.timed("beanCollection") {
            ManagementFactory.getGarbageCollectorMXBeans()
        }

        val totalCollections = beans.sumByLong { max(it.collectionCount, 0) }
        val totalCollectionTime = beans.sumByLong { max(it.collectionTime, 0) }
        val averageCollectionTime = if (totalCollections > 0 && totalCollectionTime > 0)
            totalCollectionTime / totalCollections
        else
            0

        val jvm = JSONObject()
            .put("Uptime", Utils.fTime(System.currentTimeMillis() - BoobBot.startTime))
            .put("JVM_CPU_Usage", procCpuUsage)
            .put("System_CPU_Usage", sysCpuUsage)
            .put("RAM_Usage", "${usedMB}MB($rPercent%)")
            .put("Threads", Thread.activeCount())
            .put("Total_GC_Count", totalCollections)
            .put("Total_GC_Time", "${totalCollectionTime}ms")
            .put("Avg_GC_Cycle", "${dpFormatter.format(averageCollectionTime)}ms")

        val bb = JSONObject()
            .put("Guilds", servers)
            .put("Users", users)
            .put("Audio_Players", players)
            .put("Shards_Online", "$shardsOnline/$shards")
            .put("Average_Latency", "${averageShardLatency}ms")

        return JSONObject().put("bb", bb).put("jvm", jvm)
    }

    private fun getPings(): JSONArray {
        return JSONArray().also {
            for ((jda, status) in BoobBot.shardManager.statuses) {
                it.put(mapOf(
                    "shard" to jda.shardInfo.shardId,
                    "ping" to jda.gatewayPing,
                    "status" to status
                ))
            }
        }
    }

    fun startServer() {
        embeddedServer(Netty, host = "127.0.0.1", port = 8769) {
            install(AutoHeadResponse)
            install(CallLogging) {
                level = Level.INFO
                filter { it.request.path().startsWith("/") }
            }
            // install cors broke? lets do this
            install(DefaultHeaders) {
                header("Access-Control-Allow-Origin", "*")
                header("Access-Control-Max-Age", "1728000")
                header("Access-Control-Allow-Credentials", "true")
                header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                header(
                    "Access-Control-Allow-Headers",
                    "Access-Control-Allow-Headers' 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type"
                )
                header("server", "yOu DoNt NeEd To KnOw")
            }
            install(ForwardedHeaderSupport)
            install(XForwardedHeaderSupport)
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                    disableHtmlEscaping()
                    enableComplexMapKeySerialization()
                    serializeNulls()
                    serializeSpecialFloatingPointValues()
                }
            }
            routing {
                intercept(ApplicationCallPipeline.Call) {
                    BoobBot.metrics.record(Metrics.happened("request ${call.request.path()}"))
                    BoobBot.metrics.record(Metrics.happened("requests"))

                    Utils.suspendTimed("request:${call.request.path()}") {
                        proceed()
                    }
                }

                get("/") {
                    call.respondRedirect("https://boob.bot", true)
                }

                get("/stats") {
                    call.respondText(
                        "{\"stats\": ${getStats()}}",
                        ContentType.Application.Json
                    )
                }

                get("/metrics") {
                    call.respondText("{\"metrics\": ${BoobBot.metrics.render().get()}}", ContentType.Application.Json)
                }

                get("/health") {
                    call.respondText(
                        "{\"health\": \"ok\", \"ping\": ${BoobBot.shardManager.averageGatewayPing}}",
                        ContentType.Application.Json
                    )
                }

                get("/pings") {
                    call.respondText("{\"status\": ${getPings()}}", ContentType.Application.Json)
                }

                get("/commands") {
                    val categories = hashMapOf<String, JSONArray>()

                    BoobBot.commands.values
                        .filter { it.properties.category != Category.DEV && !it.properties.hidden }
                        .forEach {
                            val category = categories.computeIfAbsent(it.properties.category.name) { JSONArray() }

                            val j = JSONObject(mapOf(
                                "command" to it.name,
                                "category" to it.properties.category,
                                "description" to it.properties.description,
                                "aliases" to "[${it.properties.aliases.joinToString(", ")}]"
                            ))

                            category.put(j)
                        }

                    val response = JSONObject(categories)
                    call.respondText("{\"commands\": $response}", ContentType.Application.Json)
                }
            }
        }.start(wait = false)
    }

    companion object {
        private val timingKey = AttributeKey<Long>("requestReceived")
    }
}
