package bot.boobbot.utils

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.User
import bot.boobbot.entities.misc.DonorType
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.apache.http.HttpHost
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Proxy
import java.nio.file.Paths
import java.util.*
import kotlin.math.floor
import kotlin.math.sqrt


object Utils {
    private val rand = Random()
    private val path = Paths.get("").toAbsolutePath().toString()
    private val ips = listOf(
        "104.247.201.235:8564", //Atlanta
        "104.247.211.200:4041", //PeachtreeCity
        "104.237.210.97:4974", //Chicago
        "172.82.172.126:1248", //Schererville
        "45.43.216.46:3568",  //Los Angeles
        "45.43.217.133:8058", //Los Angeles
        "45.58.59.214:6560", //Atlanta
        "96.46.0.53:1520", //Duarte
        "208.72.224.182:6958", //Duarte
        "67.227.66.102:7498" // Las Vegas
    )

    val version by lazy {
        val stream = BoobBot::class.java.classLoader.getResourceAsStream("version.txt")!!
        return@lazy InputStreamReader(stream).readText()
    }

    private val jsonArrays = JSONObject(
        if (File("$path/arrays.json").exists()) {
            (File("$path/arrays.json").inputStream())
                .bufferedReader()
                .use { it.readText() }
        } else {
            (BoobBot::class.java.classLoader.getResourceAsStream("arrays.json"))!!
                .bufferedReader()
                .use { it.readText() }
        }
    )

    fun checkDonor(event: Message): Boolean {
        return BoobBot.pApi.getDonorType(event.author.id).tier >= 1 // Supporter, Server Owner, Developer
                || (event.isFromGuild && BoobBot.pApi.getDonorType(event.guild.ownerId) == DonorType.SERVER_OWNER)
                || (event.isFromGuild && BoobBot.database.isPremiumServer(event.guild.id))
    }

    fun checkSlashDonor(event: SlashCommandInteractionEvent): Boolean {
        return BoobBot.pApi.getDonorType(event.user.id).tier >= 1 // Supporter, Server Owner, Developer
                || (event.isFromGuild && BoobBot.pApi.getDonorType(event.guild!!.ownerId) == DonorType.SERVER_OWNER)
                || (event.isFromGuild && BoobBot.database.isPremiumServer(event.guild!!.id))
    }
    fun getRandomFunString(key: String): String {
        val arr = jsonArrays.getJSONArray(key)
        return arr.getString(rand.nextInt(arr.length()))
    }

    fun getRandomMoan(): File {
        val arr = jsonArrays.getJSONArray("moan")
        val fileOjb = arr.getJSONObject(rand.nextInt(arr.length()))
        return File("$path/moan/${fileOjb.get("name")}.${fileOjb.get("ext")}")
    }

    fun getProxy(): Proxy {
        val proxy = ips.random()
        val parts = proxy.split(":".toRegex(), 2).toTypedArray()
        return Proxy(Proxy.Type.HTTP, InetSocketAddress(parts[0], parts[1].toInt()))
    }

    @Suppress("unused")
    fun getProxyAsHost(): HttpHost {
        val proxy = ips.random()
        val parts = proxy.split(":".toRegex(), 2).toTypedArray()
        return HttpHost(parts[0], parts[1].toInt(), "http")
    }

    fun logCommand(m: Message) {
        if (!BoobBot.logCom) {
            return
        }

        val message = "[%s/%s (%s)] %s (%s): %s"
        val origin = if (m.isFromGuild) "${m.guild.name} (${m.guild.id})" else "DM"
        BoobBot.log.info(message.format(origin, m.channel.name, m.channel.id, m.author.asTag, m.author.id, m.contentDisplay))
    }

    fun fTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000 % 60
        val minutes = milliseconds / (1000 * 60) % 60
        val hours = milliseconds / (1000 * 60 * 60) % 24
        val days = milliseconds / (1000 * 60 * 60 * 24)

        return when {
            days > 0 -> String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun readAll(inputStream: InputStream): String = InputStreamReader(inputStream).use { it.readText() }

    fun calculateLewdLevel(user: User): Int {
        val calculateLewdPoints =
            (user.experience / 100) * .1 +
                    (user.nsfwCommandsUsed / 100) * .3 -
                    (user.commandsUsed / 100) * .3 +
                    (user.lewdPoints / 100) * 20
        // lewd level up
        return floor(0.1 * sqrt(calculateLewdPoints)).toInt()
    }

    fun random(lower: Int, upper: Int) = rand.nextInt(upper - lower) + lower

    fun checkMissingPermissions(
        target: Member,
        channel: GuildChannel,
        permissions: Array<Permission>
    ): List<Permission> {
        return permissions.filter { !target.hasPermission(channel, it) }
    }
}
