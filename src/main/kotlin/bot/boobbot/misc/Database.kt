package bot.boobbot.misc

import bot.boobbot.BoobBot
import bot.boobbot.models.economy.Guild
import bot.boobbot.models.economy.Serializable
import bot.boobbot.models.economy.User
import com.google.gson.Gson
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.bson.Document
import org.jetbrains.kotlin.serialization.js.ast.JsAstProtoBuf
import java.time.Instant

class Database {

    private val gson = Gson()
    private val mongo = MongoClients.create(BoobBot.config.mongoDbUrl)

    /** Databases **/
    private val bb = mongo.getDatabase("boobbot")
    private val autoPorn = mongo.getDatabase("autoporn")

    /** Tables **/
    private val webhooks = autoPorn.getCollection("webhooks")
    private val guildSettings = bb.getCollection("settings")
    private val guildPrefix = bb.getCollection("prefix")
    private val userSettings = bb.getCollection("usersettings")
    private val customCommands = bb.getCollection("customcoms")
    private val donor = bb.getCollection("donor")
    private val guilds = bb.getCollection("guilds")
    private val users = bb.getCollection("users")


    /**
     * Webhooks/Autoporn
     */
    fun getWebhook(guildId: String): Document? {
        return webhooks.find(BasicDBObject("_id", guildId))
            .firstOrNull()
    }

    fun setWebhook(guildId: String, webhookUrl: String, category: String, channelId: String) {
        val doc = Document("webhook", webhookUrl)
            .append("category", category)
            .append("channelId", channelId)

        webhooks.updateOne(
            eq("_id", guildId),
            Document("\$set", doc),
            UpdateOptions().upsert(true)
        )
    }

    fun deleteWebhook(guildId: String) {
        webhooks.deleteOne(eq("_id", guildId))
    }


    /**
     * User
     */
    fun getUser(userId: String): User {
        return users.find(BasicDBObject("_id", userId))
            .firstOrNull()
            ?.let { deserialize<User>(it.toJson()) }
            ?: User.new(userId)
    }

    fun deleteUser(userId: String) {
        users.deleteOne(eq("_id", userId))
    }

    fun setUser(user: User) {
        user.lastSaved = Instant.now()
        users.updateOne(
            eq("_id", user._id),
            Document("\$set", serialize(user)),
            UpdateOptions().upsert(true)
        )
    }

    fun getAllUsers(): List<User> {
        return users.find()
            .map { deserialize<User>(it.toJson()) }
            .toList()
    }


    /**
     * Guild
     */
    fun getGuild(guildId: String): Guild {
        return guilds.find(BasicDBObject("_id", guildId))
            .firstOrNull()
            ?.let { deserialize<Guild>(it.toJson()) }
            ?: Guild.new(guildId)
    }

    fun deleteGuild(guildId: String) {
        guilds.deleteOne(eq("_id", guildId))
    }

    fun setGuild(guild: Guild) {
        guilds.updateOne(
            eq("_id", guild._id),
            Document("\$set", serialize(guild)),
            UpdateOptions().upsert(true)
        )
    }


    /**
     * Disable-able commands
     */
    fun getDisabledCommands(guildId: String): List<String> {
        val s = guildSettings.find(BasicDBObject("_id", guildId))
            .firstOrNull() ?: return emptyList()

        return s.getList("disabled", String::class.java, emptyList())
    }

    fun disableCommands(guildId: String, commands: List<String>) {
        guildSettings.updateOne(
            eq("_id", guildId),
            Updates.addEachToSet("disabled", commands),
            UpdateOptions().upsert(true)
        )
    }

    fun enableCommands(guildId: String, commands: List<String>) {
        guildSettings.updateOne(
            eq("_id", guildId),
            Updates.pullAll("disabled", commands),
            UpdateOptions().upsert(true)
        )
    }

    fun getDisabledForChannel(guildId: String, channelId: String): List<String> {
        val s = guildSettings.find(BasicDBObject("_id", guildId))
            .firstOrNull() ?: return emptyList()

        val allDisabled = s.getList("channelDisabled", Document::class.java, emptyList())

        return allDisabled.filter { it.getString("channelId") == channelId }
            .map { it.getString("name") }
    }

    fun disableForChannel(guildId: String, channelId: String, commands: List<String>) {
        val toAdd = commands.map { Document("name", it).append("channelId", channelId) }

        guildSettings.updateOne(
            eq("_id", guildId),
            Updates.addEachToSet("channelDisabled", toAdd),
            UpdateOptions().upsert(true)
        )
    }

    fun enableForChannel(guildId: String, channelId: String, commands: List<String>) {
        val toRemove = commands.map { Document("name", it).append("channelId", channelId) }

        guildSettings.updateOne(
            eq("_id", guildId),
            Updates.pullAll("channelDisabled", toRemove),
            UpdateOptions().upsert(true)
        )
    }


    /**
     * Custom commands
     */
    fun addCustomCommand(guildId: String, name: String, content: String) {
        val tag = Document("name", name)
            .append("content", content)

        customCommands.updateOne(
            eq("_id", guildId),
            Updates.addToSet("cc", tag),
            UpdateOptions().upsert(true)
        )
    }

    fun removeCustomCommand(guildId: String, name: String) {
        customCommands.updateOne(
            eq("_id", guildId),
            Updates.pull("cc", BasicDBObject("name", name)),
            UpdateOptions().upsert(true)
        )
    }

    fun getCustomCommands(guildId: String): Map<String, String> {
        val obj = customCommands.find(BasicDBObject("_id", guildId))
            .firstOrNull() ?: return emptyMap()

        val commands = obj.getList("cc", Document::class.java)
        return commands.associateBy({ it.getString("name") }, { it.getString("content") })
    }

    fun findCustomCommand(guildId: String, name: String): String? {
        val custom = getCustomCommands(guildId)
        return custom.filter { it.key == name }.values.firstOrNull()
    }


    /**
     * Guild Prefix
     */
    fun getPrefix(guildId: String): String? {
        val custom = guildPrefix.find(BasicDBObject("_id", guildId))
            .firstOrNull()
            ?: return null

        val prefix = custom["prefix"]!!

        if (String::class.java.isAssignableFrom(prefix::class.java)) {
            return custom.getString("prefix")
        }

        val prefixes = custom.getList("prefix", String::class.java)
        return prefixes.firstOrNull()
    }


    fun getDonor(userId: String) = get(donor, userId, "pledge", 0.0)
    fun setDonor(userId: String, pledge: Double) = set(donor, userId, "pledge", pledge)
    fun removeDonor(userId: String) = remove(donor, userId)
    fun getAllDonors() = donor.find()
        .associateBy({ it.getString("_id") }, { it.getDouble("pledge") })

    fun removePrefix(guildId: String) = remove(guildPrefix, guildId)
    fun setPrefix(guildId: String, prefix: String) = set(guildPrefix, guildId, "prefix", prefix)

    fun getCanUserReceiveNudes(userId: String) = get(userSettings, userId, "nudes", false)
    fun setUserCanReceiveNudes(userId: String, canReceive: Boolean) = set(userSettings, userId, "nudes", canReceive)

    fun getUserCockBlocked(userId: String) = get(userSettings, userId, "cockblocked", false)
    fun setUserCockBlocked(userId: String, cockblocked: Boolean) =
        set(userSettings, userId, "cockblocked", cockblocked)

    fun getUserAnonymity(userId: String) = get(userSettings, userId, "anonymity", false)
    fun setUserAnonymity(userId: String, anonymity: Boolean) = set(userSettings, userId, "anonymity", anonymity)


    /**
     * Common Functions
     */
    private inline fun <reified T> get(
        c: MongoCollection<Document>, id: String,
        key: String, default: T
    ): T {
        return c.find(BasicDBObject("_id", id))
            .firstOrNull()
            ?.get(key, T::class.java)
            ?: default
    }

    private fun set(c: MongoCollection<Document>, id: String, k: String, v: Any) {
        val doc = Document(k, v)

        c.updateOne(
            eq("_id", id),
            Document("\$set", doc),
            UpdateOptions().upsert(true)
        )
    }

    private fun remove(c: MongoCollection<Document>, id: String) {
        c.deleteOne(eq("_id", id))
    }

    private inline fun <reified T> deserialize(json: String): T {
        return gson.fromJson(json, T::class.java)
    }

    private fun serialize(entity: Any): Document {
        return Document.parse(gson.toJson(entity))
    }
}
