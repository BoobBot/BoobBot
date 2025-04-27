//package bot.boobbot.entities.internals
//
//import bot.boobbot.BoobBot
//import bot.boobbot.entities.db.Guild
//import bot.boobbot.entities.db.User
//import bot.boobbot.entities.db.WebhookConfiguration
//import com.google.gson.Gson
//import com.mongodb.BasicDBObject
//import com.mongodb.ConnectionString
//import com.mongodb.MongoClientSettings
//import com.mongodb.client.MongoClients
//import com.mongodb.client.MongoCollection
//import com.mongodb.client.model.Filters.and
//import com.mongodb.client.model.Filters.eq
//import com.mongodb.client.model.UpdateOptions
//import com.mongodb.client.model.Updates
//import org.bson.Document
//import org.json.JSONObject
//import java.time.Instant
//import java.util.concurrent.TimeUnit
//
//class Database {
//    var allReads = 0L
//    var guildReads = 0L
//
//    private val gson = Gson()
//    private val mongo = MongoClients.create(
//        MongoClientSettings.builder().apply {
//            applicationName("BoobBot")
//            applyConnectionString(ConnectionString(BoobBot.config.MONGO_DB_URL))
//            applyToConnectionPoolSettings {
//                it.maxSize(BoobBot.config.SHARD_TOTAL)
//            }
//            applyToSocketSettings {
//                it.connectTimeout(10, TimeUnit.SECONDS)
//                it.readTimeout(10, TimeUnit.SECONDS)
//            }
//        }.build()
//    )
//
//    /** Databases **/
//    private val bb = mongo.getDatabase("boobbot")
//    private val autoPorn = mongo.getDatabase("autoporn")
//
//    /** Tables **/
//    private val webhooks = autoPorn.getCollection("webhooks")
//    private val webhooksv2 = autoPorn.getCollection("webhooksv2")
//    private val guilds = bb.getCollection("guilds")
//    private val users = bb.getCollection("users")
//
//
//    /**
//     * Webhooks/Autoporn
//     */
//    fun getWebhooks(guildId: String): List<WebhookConfiguration> {
//        allReads++
//
//        val legacy = getWebhookAndMigrate(guildId)
//
//        if (legacy != null) {
//            return legacy.let { listOf(WebhookConfiguration(it.getString("category"), it.getLong("channelId"), it.getString("webhook"))) }
//        }
//
//        return webhooksv2.find(eq("_id", guildId))
//            .firstOrNull()?.getList("webhooks", Document::class.java)
//            ?.map { deserialize(it.toJson()) }
//            ?: emptyList()
//    }
//
//    @Deprecated("Use getWebhook(guildId, channelId)", replaceWith = ReplaceWith("getWebhook(guildId, channelId)"))
//    fun getWebhookAndMigrate(guildId: String): Document? {
//        allReads++
//        val document = webhooks.find(BasicDBObject("_id", guildId)).firstOrNull()
//
//        if (document != null) {
//            deleteWebhook(guildId)
//            setWebhook(guildId, document.getString("webhook"), document.getString("category"), document.getString("channelId"))
//        }
//
//        return document
//    }
//
//    fun setWebhook(guildId: String, webhookUrl: String, category: String, channelId: String) {
//        val doc = Document("webhook", webhookUrl)
//            .append("category", category)
//            .append("channelId", channelId)
//
//        webhooksv2.updateOne(
//            eq("_id", guildId),
//            Updates.push("webhooks", doc),
//            UpdateOptions().upsert(true)
//        )
//    }
//
//    fun deleteWebhook(guildId: String) {
//        webhooks.deleteOne(eq("_id", guildId))
//    }
//
//    fun deleteWebhookV2(guildId: String, channelId: String, category: String? = null) {
//        val predicates = category?.let { and(eq("channelId", channelId), eq("category", it)) }
//            ?: eq("channelId", channelId)
//
//        webhooksv2.updateOne(
//            eq("_id", guildId),
//            Updates.pull("webhooks", predicates)
//        )
//    }
//
//    fun clearWebhooks(guildId: String) {
//        webhooksv2.deleteOne(eq("_id", guildId))
//    }
//
//
//    /**
//     * User
//     */
//    fun getUser(userId: String): User {
//        allReads++
//        return users.find(BasicDBObject("_id", userId))
//            .firstOrNull()
//            ?.let { deserialize<User>(it.toJson()) }
//            ?: User(userId)
//    }
//
//    fun deleteUser(userId: String) = remove(users, userId)
//
//    fun setUser(user: User) {
//        user.lastSaved = Instant.now()
//        users.updateOne(
//            eq("_id", user._id),
//            Document("\$set", serialize(user)),
//            UpdateOptions().upsert(true)
//        )
//    }
//
//
//    /**
//     * Guild
//     */
//    fun getGuild(guildId: String): Guild {
//        allReads++
//        guildReads++
//        return guilds.find(BasicDBObject("_id", guildId))
//            .firstOrNull()
//            ?.let { Guild.fromJson(JSONObject(it.toJson())) } //deserialize<Guild>(it.toJson()) }
//            ?: Guild(guildId)
//    }
//
//    fun deleteGuild(guildId: String) = remove(guilds, guildId)
//
//    fun setGuild(guild: Guild) {
//        guilds.updateOne(
//            eq("_id", guild._id),
//            Document("\$set", serialize(guild)),
//            UpdateOptions().upsert(true)
//        )
//    }
//
//
//    /**
//     * Disable-able commands
//     */
//    fun getDisabledCommands(guildId: String): List<String> {
//        return getGuild(guildId).disabled.toList()
//    }
//
//    fun disableCommands(guildId: String, commands: List<String>) {
//        guilds.updateOne(
//            eq("_id", guildId),
//            Updates.addEachToSet("disabled", commands),
//            UpdateOptions().upsert(true)
//        )
//    }
//
//    fun enableCommands(guildId: String, commands: List<String>) {
//        guilds.updateOne(
//            eq("_id", guildId),
//            Updates.pullAll("disabled", commands),
//            UpdateOptions().upsert(true)
//        )
//    }
//
//    fun getDisabledForChannel(guildId: String, channelId: String): List<String> {
//        return getGuild(guildId).channelDisabled.filter { it.channelId == channelId }.map { it.name }
//    }
//
//    fun disableForChannel(guildId: String, channelId: String, commands: List<String>) {
//        val toAdd = commands.map { Document("name", it).append("channelId", channelId) }
//
//        guilds.updateOne(
//            eq("_id", guildId),
//            Updates.addEachToSet("channelDisabled", toAdd),
//            UpdateOptions().upsert(true)
//        )
//    }
//
//    fun enableForChannel(guildId: String, channelId: String, commands: List<String>) {
//        val toRemove = commands.map { Document("name", it).append("channelId", channelId) }
//
//        guilds.updateOne(
//            eq("_id", guildId),
//            Updates.pullAll("channelDisabled", toRemove),
//            UpdateOptions().upsert(true)
//        )
//    }
//
//
//    /**
//     * Custom commands
//     */
//    fun addCustomCommand(guildId: String, name: String, content: String) {
//        val tag = Document("name", name)
//            .append("content", content)
//
//        guilds.updateOne(
//            eq("_id", guildId),
//            Updates.addToSet("cc", tag),
//            UpdateOptions().upsert(true)
//        )
//    }
//
//    fun removeCustomCommand(guildId: String, name: String) {
//        guilds.updateOne(
//            eq("_id", guildId),
//            Updates.pull("cc", BasicDBObject("name", name)),
//            UpdateOptions().upsert(true)
//        )
//    }
//
//    fun getCustomCommands(guildId: String): Map<String, String> {
//        return getGuild(guildId).customCommands.associateBy({ it.name }, { it.content })
//    }
//
//    fun findCustomCommand(guildId: String, name: String): String? {
//        return getCustomCommands(guildId)[name]
//    }
//
//    fun getDonor(userId: String) = genericGet(users, userId, "pledge", "0.0")?.toString()?.toDoubleOrNull() ?: 0.0
//    fun setDonor(userId: String, pledge: Double) = set(users, userId, "pledge", pledge)
//    fun getAllDonors() = users.find()
//        .associateBy({ it.getString("_id") }, { it.getDouble("pledge") })
//
//    fun isPremiumServer(guildId: String) = get(guilds, guildId, "redeemer", -1L) != -1L
//    fun setPremiumServer(guildId: String, redeemerId: Long) = set(guilds, guildId, "redeemer", redeemerId)
//    fun getPremiumServers(redeemerId: Long) = guilds.find(BasicDBObject("redeemer", redeemerId))
//        .map { Guild.fromJson(JSONObject(it.toJson())) }
//        .toList()
//
//    fun getAllUsers(): MongoCollection<Document> = users
//
//    fun getCanUserReceiveNudes(userId: String) = get(users, userId, "nudes", false)
//    fun setUserCanReceiveNudes(userId: String, canReceive: Boolean) = set(users, userId, "nudes", canReceive)
//
//    fun getUserCockBlocked(userId: String) = get(users, userId, "cockblocked", false)
//    fun setUserCockBlocked(userId: String, cockblocked: Boolean) =
//        set(users, userId, "cockblocked", cockblocked)
//
//    fun getUserAnonymity(userId: String) = get(users, userId, "anonymity", false)
//    fun setUserAnonymity(userId: String, anonymity: Boolean) = set(users, userId, "anonymity", anonymity)
//
//
//    /**
//     * Common Functions
//     */
//    private inline fun <reified T> get(
//        c: MongoCollection<Document>, id: String,
//        key: String, default: T
//    ): T {
//        return c.find(BasicDBObject("_id", id))
//            .firstOrNull()
//            ?.get(key, T::class.java)
//            ?: default
//    }
//
//    private fun genericGet(
//        c: MongoCollection<Document>, id: String,
//        key: String, default: Any?
//    ): Any? {
//        return c.find(BasicDBObject("_id", id))
//            .firstOrNull()
//            ?.get(key)
//            ?: default
//    }
//
//    private fun set(c: MongoCollection<Document>, id: String, k: String, v: Any) {
//        val doc = Document(k, v)
//
//        c.updateOne(
//            eq("_id", id),
//            Document("\$set", doc),
//            UpdateOptions().upsert(true)
//        )
//    }
//
//    private fun remove(c: MongoCollection<Document>, id: String) {
//        c.deleteOne(eq("_id", id))
//    }
//
//    private inline fun <reified T> deserialize(json: String): T = gson.fromJson(json, T::class.java)
//    private fun serialize(entity: Any): Document = Document.parse(gson.toJson(entity))
//}
