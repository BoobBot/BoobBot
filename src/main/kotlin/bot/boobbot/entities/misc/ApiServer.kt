package bot.boobbot.entities.misc

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.ExecutableCommand
import bot.boobbot.entities.framework.impl.SubCommandWrapper
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.TimerUtil
import bot.boobbot.utils.Utils
import com.google.gson.Gson
import com.sun.management.OperatingSystemMXBean
import de.mxro.metrics.jre.Metrics
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.api.utils.data.DataArray
import net.dv8tion.jda.api.utils.data.DataObject
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue
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
        authorizeUrl = BoobBot.config.DISCORD_AUTH_URL, // OAuth authorization endpoint
        accessTokenUrl = BoobBot.config.DISCORD_TOKEN_URL, // OAuth token endpoint
        clientId = if (BoobBot.isDebug) "285480424904327179" else BoobBot.selfId.toString(),
        clientSecret = BoobBot.config.DISCORD_CLIENT_SECRET,
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
            .put("Users", BoobBot.shardManager.guilds.sumOf { it.memberCount })
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


    val allSlashCommands = BoobBot.commands.values.filter { it.slashEnabled }
    val categorised = allSlashCommands.filter { it.category != null }.groupBy { it.category!! }.map(::buildCategory)
    val remaining = allSlashCommands.filter { it.category == null }.map(::buildCommand)

    private fun buildCommand(cmd: ExecutableCommand): DataObject {
        val isNsfw = cmd.properties.nsfw

        val slash = Commands.slash(cmd.name.lowercase(), cmd.properties.description)
            .also { it.isGuildOnly = cmd.properties.guildOnly }
            .also { buildOptions(cmd.options).also(it::addOptions) }
            .toData()
            .also { data -> isNsfw.ifTrue { data.put("nsfw", true) } }

        if (cmd.subcommands.isNotEmpty()) {
            slash.getArray("options").addAll(cmd.subcommands.values.map(::buildSubcommand))
        }

        return slash
    }

    private fun buildCategory(entry: Map.Entry<String, List<ExecutableCommand>>): DataObject {
        val (category, cmds) = entry

        if (cmds.size > 25) {
            throw IllegalArgumentException("Cannot have more than 25 subcommands/groups per command!")
        }

        val isNsfw = entry.value.any { it.properties.nsfw }
        val isGuildOnly = entry.value.all { it.properties.guildOnly }

        val slash = Commands.slash(category, "$category commands")
            .also { it.isGuildOnly = isGuildOnly }
            .toData()
            .also { data -> isNsfw.ifTrue { data.put("nsfw", true) } }

        for (cmd in cmds) {
            if (cmd.subcommands.isNotEmpty()) {
                val group = SubcommandGroupData(cmd.name, cmd.properties.description).toData()
                val scs = cmd.subcommands.values.map(::buildSubcommand)

                group.getArray("options").addAll(scs)
                slash.getArray("options").add(group)
                continue
            }

            val sc = SubcommandData(cmd.name, cmd.properties.description)
                .also { buildOptions(cmd.options).also(it::addOptions) }
                .toData()

            slash.getArray("options").add(sc)
        }

        return slash
    }

    private fun buildSubcommand(cmd: SubCommandWrapper): DataObject {
        return SubcommandData(cmd.name.lowercase(), cmd.description)
            .also { buildOptions(cmd.options).also(it::addOptions) }
            .toData()
    }

    private fun buildOptions(options: List<Option>): List<OptionData> {
        return options.map {
            OptionData(it.type, it.name, it.description, it.required).also { data ->
                for (choice in it.choices) {
                    data.addChoice(choice.name, choice.value)
                }
            }
        }
    }

    class UserSession(val id: String, val avatar: String, val username: String, val discriminator: String)

    fun startServer() {
        embeddedServer(Netty, host = "127.0.0.1", port = 8769) {
            install(Sessions) {
                cookie<UserSession>("SessionId") {
                    val secretSignKey = hex(BoobBot.config.SESSION_KEY)
                    transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
                }
            }

            install(Authentication) {
                oauth("discord") {
                    client = HttpClient(Apache)
                    providerLookup = { clientSettings }
                    urlProvider = { BoobBot.config.OAUTH_REDIRECT_URL }
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
            install(ForwardedHeaders)
            install(XForwardedHeaders)
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
                        val data = HttpClient(Apache).get("https://discordapp.com/api/users/@me") {
                            header("Authorization", "Bearer ${principal!!.accessToken}")
                        }
                            .body<String>()
                        val s = Gson().fromJson(data, UserSession::class.java)
                        call.sessions.set(s)
                        call.respondRedirect("/admin")
                    }
                }

                get("/admin") {
                    val s = call.sessions.get<UserSession>() ?: return@get call.respondRedirect("/oauth")

                    if (!Config.OWNERS.contains(s.id.toLong())) {
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
                        "{\"health\": \"$health\", \"ping\": ${BoobBot.shardManager.averageGatewayPing.toInt()}}",
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

                get("/slashjson") {
                    val json = DataArray.empty()
                        .addAll(categorised)
                        .addAll(remaining)
                        .toPrettyString()
                    call.respondText("{\"json\": $json}", ContentType.Application.Json)
                }

                get("/slash") {
                    val json = DataArray.empty()
                        .addAll(categorised)
                        .addAll(remaining)
                        .toPrettyString()
                    val slash_html = "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "<title>SLash Commands</title>\n" +
                            "<link rel=stylesheet href=https://cdn.jsdelivr.net/npm/pretty-print-json@1.2/dist/pretty-print-json.dark-mode.css>\n" +
                            "<script src=https://cdn.jsdelivr.net/npm/pretty-print-json@1.2/dist/pretty-print-json.min.js></script>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<pre id=commands class=json-container></pre>\n" +
                            "<script>\n" +
                            "const elem = document.getElementById('commands');\n" +
                            "elem.innerHTML = prettyPrintJson.toHtml($json);\n" +
                            "</script>\n" +
                            "</body>\n" +
                            "</html>\n"
                    call.respondText(slash_html, ContentType.Text.Html)
                }

            }
        }.start(wait = false)
    }

    companion object {
        val dpFormatter = DecimalFormat("0.00")
    }
}