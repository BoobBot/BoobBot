package bot.boobbot.misc

import bot.boobbot.BoobBot
import com.sun.management.OperatingSystemMXBean
import de.mxro.metrics.jre.Metrics
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.RequestConnectionPoint
import io.ktor.request.path
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.dv8tion.jda.core.JDA
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.event.Level
import java.lang.management.ManagementFactory
import java.text.DecimalFormat


typealias DelayProvider = suspend (ms: Int) -> Unit

class ApiServer {
    fun startServer() {

        /* fun isJSONValid(json: String): Boolean {
             try {
                 JSONObject(json)
             } catch (ex: JSONException) {
                 try {
                     JSONArray(json)
                 } catch (ex1: JSONException) {
                     return false
                 }

             }

             return true
         }*/

        fun getStats(): JSONObject {
            val dpFormatter = DecimalFormat("0.00")
            val rUsedRaw = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            val rPercent = dpFormatter.format(rUsedRaw.toDouble() / Runtime.getRuntime().totalMemory() * 100)
            val usedMB = dpFormatter.format(rUsedRaw.toDouble() / 1048576)

            val servers = BoobBot.shardManager.guildCache.size()
            val users = BoobBot.shardManager.userCache.size()

            val shards = BoobBot.shardManager.shardsTotal
            val shardsOnline =
                BoobBot.shardManager.shards.asSequence().filter { s -> s.status == JDA.Status.CONNECTED }
                    .count()
            val averageShardLatency = BoobBot.shardManager.averagePing.toInt()

            val osBean: OperatingSystemMXBean =
                ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)
            val procCpuUsage = dpFormatter.format(osBean.processCpuLoad * 100)
            val sysCpuUsage = dpFormatter.format(osBean.systemCpuLoad * 100)
            val players = BoobBot.musicManagers.filter { p -> p.value.player.playingTrack != null }.count()

            var totalGarbageCollections = 0L
            var garbageCollectionTime = 0L
            val totalGarbageCollectionTime: Long
            ManagementFactory.getGarbageCollectorMXBeans().forEach { gc ->
                val count = gc.collectionCount
                if (count >= 0) {
                    totalGarbageCollections += count
                }
                val time = gc.collectionTime
                if (time >= 0) {
                    garbageCollectionTime += time
                }
            }
            totalGarbageCollectionTime = if (garbageCollectionTime > 0 && totalGarbageCollections > 0) {
                garbageCollectionTime / totalGarbageCollections
            } else {
                0L
            }

            val jvm = JSONObject()
                .put("Uptime", Utils.fTime(System.currentTimeMillis() - BoobBot.startTime))
                .put("JVM_CPU_Usage", procCpuUsage)
                .put("System_CPU_Usage", sysCpuUsage)
                .put("RAM_Usage", "${usedMB}MB($rPercent%)")
                .put("Threads", Thread.activeCount())
                .put("Total_GC_Count", totalGarbageCollections)
                .put("Total_GC_Time", "${garbageCollectionTime}ms")
                .put("Avg_GC_Cycle", "${dpFormatter.format(totalGarbageCollectionTime)}ms")

            val bb = JSONObject()
                .put("Guilds", servers)
                .put("Users", users)
                .put("Audio_Players", players)
                .put("Auto_Porn_Channels", BoobBot.autoPornChannels)
                .put("Shards_Online", "$shardsOnline/$shards")
                .put("Average_Latency", "${averageShardLatency}ms")

            return JSONObject().put("bb", bb).put("jvm", jvm)
        }

        fun getPings(): JSONArray {
            val pings = JSONArray()
            for (e in BoobBot.shardManager.statuses.entries) pings.put(
                JSONObject().put(
                    "shard",
                    e.key.shardInfo.shardId
                ).put("ping", e.key.ping).put("status", e.value)
            )
            return pings
        }

        embeddedServer(Netty, 8888) {
            install(AutoHeadResponse)
            install(CallLogging) {
                level = Level.INFO
                filter { call -> call.request.path().startsWith("/") }
            }
            // insatll cors broke? lets do this
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
            /*install(WebSockets) {
                pingPeriod = Duration.ofSeconds(60)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }*/

            routing {
                /* just things
                   val baos = ByteArrayOutputStream()
                   System.setOut(PrintStream(baos))
                   webSocket("/ws") {
                       BoobBot.metrics.record(Metrics.happened("ws connected"))
                       if (BoobBot.isDebug){ BoobBot.log.info("incoming ws connect from ${call.request.origin.host}") }
                       incoming.mapNotNull { it as? Frame.Text }.consumeEach { frame ->
                           val inText = frame.readText()
                           BoobBot.metrics.record(Metrics.happened("incoming frame"))
                           if (BoobBot.isDebug){ BoobBot.log.info("incoming ws frame \n text: ${frame.readText()}") }
                           if (inText.equals("bye", ignoreCase = true)) {
                               close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                           }
                           if (inText.equals("metrics", ignoreCase = true)) {
                               outgoing.send(Frame.Text("{\"data\": ${BoobBot.metrics.render().get()}}"))
                           }
                           if (isJSONValid(inText)) {
                               val json = JSONObject(inText)
                               if (json.has("data")){
                                   BoobBot.log.info(json.getString("data"))
                                   if (json.get("data").toString().equals("metrics", ignoreCase = true)) {
                                       outgoing.send(Frame.Text("{\"data\": ${BoobBot.metrics.render().get()}}"))
                                   }
                               }

                           }
                           outgoing.send(Frame.Text("{\"this\": $inText}"))
                           baos.use {
                               outgoing.send(Frame.Text("{\"data\": ${it.toString()}}")) }
                       }

                   }*/



                get("/") {
                    BoobBot.metrics.record(Metrics.happened("request /"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    call.respondRedirect("https://boob.bot", true)
                }

                get("/bad-request") {
                    BoobBot.metrics.record(Metrics.happened("request /bad-request"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    val uri = call.request.uri
                    BoobBot.log.info("bad-Request uri: $uri")
                    val local: RequestConnectionPoint = call.request.local
                    val origin: RequestConnectionPoint = call.request.origin
                    BoobBot.log.info("${origin.host} ${local.host} ${origin.remoteHost}")
                    call.respond("no")

                }

                get("/stats") {
                    BoobBot.metrics.record(Metrics.happened("request /stats"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    call.respondText(
                        "{\"stats\": ${getStats()}}",
                        ContentType.Application.Json
                    )
                }


                get("/metrics") {
                    BoobBot.metrics.record(Metrics.happened("request /metrics"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    call.respondText("{\"metrics\": ${BoobBot.metrics.render().get()}}", ContentType.Application.Json)
                }

                get("/health") {
                    BoobBot.metrics.record(Metrics.happened("request /health"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    call.respondText(
                        "{\"health\": \"ok\", \"ping\": ${BoobBot.shardManager.averagePing}}",
                        ContentType.Application.Json
                    )
                }

                get("/pings") {
                    BoobBot.metrics.record(Metrics.happened("request /pings"))
                    BoobBot.metrics.record(Metrics.happened("requests"))

                    call.respondText("{\"status\": ${getPings()}}", ContentType.Application.Json)
                }

                get("/commands") {
                    BoobBot.metrics.record(Metrics.happened("request /commands"))
                    BoobBot.metrics.record(Metrics.happened("requests"))
                    val response = JSONObject()
                    BoobBot.commands.values.filter { command -> command.properties.category.name != "DEV" }
                        .forEach { command ->
                            if (!response.has(command.properties.category.name)) {
                                response.put(command.properties.category.name, JSONArray())
                            }
                            val array = response.getJSONArray(command.properties.category.name)
                            array.put(
                                JSONObject()
                                    .put("command", command.name)
                                    .put("category", command.properties.category)
                                    .put("description", command.properties.description)
                                    .put("aliases", "[${command.properties.aliases.joinToString(", ")}]")
                            )

                        }
                    call.respondText("{\"commands\": $response}", ContentType.Application.Json)
                }

            }

        }.start(wait = false)
    }
}