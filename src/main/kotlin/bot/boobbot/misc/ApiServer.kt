package bot.boobbot.misc

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.models.Config
import com.google.gson.Gson
import com.sun.management.OperatingSystemMXBean
import de.mxro.metrics.jre.Metrics
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.request.path
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.hex
import net.dv8tion.jda.api.JDA
import org.jetbrains.kotlin.utils.addToStdlib.sumByLong
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.event.Level
import java.lang.management.ManagementFactory
import java.text.DecimalFormat
import kotlin.math.max


class ApiServer {

    private val clientSettings = OAuthServerSettings.OAuth2ServerSettings(
        name = "discord",
        authorizeUrl = BoobBot.config.discordAuthUrl, // OAuth authorization endpoint
        accessTokenUrl = BoobBot.config.discordTokenUrl, // OAuth token endpoint
        clientId = if (BoobBot.isDebug) "285480424904327179" else BoobBot.selfId.toString(),
        clientSecret = BoobBot.config.discordClientSecret,
        // basic auth implementation is not "OAuth style" so falling back to post body
        accessTokenRequiresBasicAuth = false,
        requestMethod = HttpMethod.Post, // must POST to token endpoint
        defaultScopes = listOf("email", "identify") // what scopes to explicitly request
    )


    private fun getStats(): JSONObject {
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
            .put("Audio_Players", players)
            .put("Shards_Online", "$shardsOnline/$shards")
            .put("Guilds", BoobBot.shardManager.guilds.size)
            .put("Users", BoobBot.shardManager.guilds.sumBy { it.memberCount })
            .put("Average_Latency", "${averageShardLatency}ms")

        return JSONObject().put("bb", bb).put("jvm", jvm)
    }

    private fun getPings(): JSONArray {
        return JSONArray().also {
            for ((jda, status) in BoobBot.shardManager.statuses) {
                val obj = JSONObject()
                    .put("shard", jda.shardInfo.shardId)
                    .put("ping", jda.gatewayPing)
                    .put("status", status)

                it.put(obj)
            }
        }
    }

    class UserSession(val id: String, val avatar: String, val username: String, val discriminator: String)

    @KtorExperimentalAPI
    fun startServer() {
        embeddedServer(Netty, host = "127.0.0.1", port = 8769) {

            install(Sessions) {
                cookie<UserSession>("SessionId") {
                    val secretSignKey = hex(BoobBot.config.SessionKey)
                    transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
                }
            }

            install(Authentication) {
                oauth("discord") {
                    client = HttpClient(Apache)
                    providerLookup = { clientSettings }
                    urlProvider = { BoobBot.config.RedirectUrl }
                }
            }

            install(AutoHeadResponse)
            if (BoobBot.logCom) {
                install(CallLogging) {
                    level = Level.INFO
                    filter { it.request.path().startsWith("/") }
                }
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
                    BoobBot.metrics.record(Metrics.happened("api requests"))
                    if (BoobBot.logCom) {
                        BoobBot.metrics.record(Metrics.happened("request ${call.request.path()}"))
                        TimerUtil.inlineSuspended("request:${call.request.path()}") {
                            proceed()
                        }
                    }
                }

                get("/") {
                    call.respondRedirect("https://boob.bot", true)
                }

                authenticate("discord") {
                    get("/oauth") {
                        val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                        val data = HttpClient(Apache).get<String>("https://discordapp.com/api/users/@me") {
                            header("Authorization", "Bearer ${principal!!.accessToken}")
                        }
                        val s = Gson().fromJson(data, UserSession::class.java)
                        call.sessions.set(s)
                        call.respondRedirect("/admin")
                    }
                }

                get("/admin") {
                    val s = call.sessions.get<UserSession>() ?: return@get call.respondRedirect("/oauth")

                    if (!Config.owners.contains(s.id.toLong())) {
                        error("401")
                    }
                    call.respondText("{\"user\": ${Gson().toJson(s)}}", ContentType.Application.Json)

                }

                get("/stats") {
                    call.respondText(
                        "{\"stats\": ${getStats()}}",
                        ContentType.Application.Json
                    )
                }

                get("/metrics") {
                    call.respondText(
                        "{\"metrics\": ${BoobBot.metrics.render().get()}}",
                        ContentType.Application.Json
                    )
                }

                get("/health") {
                    val health = if (BoobBot.shardManager.allShardsConnected) "ok" else "warn"
                    call.respondText(
                        "{\"health\": $health, \"ping\": ${BoobBot.shardManager.averageGatewayPing}}",
                        ContentType.Application.Json
                    )
                }

                get("/pings") {
                    call.respondText("{\"status\": ${getPings()}}", ContentType.Application.Json)
                }

                get("/user/{userId}") {
                    val userId = call.parameters["userId"]
                    if (userId.isNullOrBlank()) call.respondText("{\"msg\": 404}", ContentType.Application.Json)
                    val user = BoobBot.database.getUser(userId!!)
                    call.respondText("{\"user\": ${Gson().toJson(user)}}", ContentType.Application.Json)
                }

                get("/commands") {
                    val categories = hashMapOf<String, JSONArray>()

                    BoobBot.commands.values
                        .filter { it.properties.category != Category.DEV && !it.properties.hidden }
                        .forEach {
                            val category = categories.computeIfAbsent(it.properties.category.name) { JSONArray() }
                            val j = JSONObject()
                                .put("command", it.name)
                                .put("category", it.properties.category)
                                .put("description", it.properties.description)
                                .put("aliases", "[${it.properties.aliases.joinToString(", ")}]")

                            category.put(j)
                        }

                    val response = JSONObject(categories)
                    call.respondText("{\"commands\": $response}", ContentType.Application.Json)
                }
            }
        }.start(wait = false)
    }

    companion object {
        val dpFormatter = DecimalFormat("0.00")
    }
}