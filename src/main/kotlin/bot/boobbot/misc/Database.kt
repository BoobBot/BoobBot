package bot.boobbot.misc

import bot.boobbot.BoobBot
import com.google.gson.Gson
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.bson.Document

class Database {

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


    //user


    data class User(
        val _id: String,
        var blacklisted: Boolean,
        var experience: Int,
        var level: Int,
        var lewdPoints: Int,
        var lewdLevel: Int,
        var messagesSent: Int,
        var nsfwMessagesSent: Int,
        var commandsUsed: Int,
        var nsfwCommandsUsed: Int,
        var bankBalance: Int,
        var balance: Int,
        var bonusXp: Int?,
        var protected: Boolean?,
        var inJail: Boolean,
        var jailRemaining: Int,
        var coolDownCount: Int
    ){
        fun save(){
            BoobBot.database.setUser(this)
        }

        fun toJson(): String {
            return Gson().toJson(this)
        }

        fun toDoc(): Document {
            return Document.parse(toJson())
        }
    }


    fun getUser(userId: String): User {
        val doc = users.find(BasicDBObject("_id", userId))
            .firstOrNull()
        return if (doc.isNullOrEmpty()) {
            val user = User(
                _id = userId,
                blacklisted = false,
                experience = 0,
                level = 0,
                lewdPoints = 0,
                lewdLevel = 0,
                messagesSent = 0,
                nsfwMessagesSent = 0,
                commandsUsed = 0,
                nsfwCommandsUsed = 0,
                bankBalance = 0,
                balance = 0,
                bonusXp = 0,
                protected = false,
                inJail = false,
                jailRemaining = 0,
                coolDownCount = 0
            )
            user.save()
            user
        } else {
            val user = User(
                doc["_id"].toString(),
                doc["blacklisted"] as Boolean,
                doc["experience"] as Int,
                doc["level"] as Int,
                doc["lewdPoints"] as Int,
                doc["lewdLevel"] as Int,
                doc["messagesSent"] as Int,
                doc["nsfwMessagesSent"] as Int,
                doc["commandsUsed"] as Int,
                doc["nsfwCommandsUsed"] as Int,
                doc["bankBalance"] as Int,
                doc["balance"] as Int,
                doc["bonusXp"] as Int,
                doc["protected"] as Boolean,
                doc["inJail"] as Boolean,
                doc["jailRemaining"] as Int,
                doc["coolDownCount"] as Int

            )
            user
        }
    }


    fun delUser(userId: String) {
        users.deleteOne(eq("_id", userId))
    }

    fun setUser(user: User) {
        users.updateOne(
            eq("_id", user._id),
            Document("\$set", user.toDoc()),
            UpdateOptions().upsert(true)
        )
    }

    fun getAllUsers(): ArrayList<User> {
        val ul = ArrayList<User>()
        val u = users.find().associateBy { it.getString("_id") }
        u.forEach { (t, doc) ->
            ul.add(
                User(
                    doc["_id"].toString(),
                    doc["blacklisted"] as Boolean,
                    doc["experience"] as Int,
                    doc["level"] as Int,
                    doc["lewdPoints"] as Int,
                    doc["lewdLevel"] as Int,
                    doc["messagesSent"] as Int,
                    doc["nsfwMessagesSent"] as Int,
                    doc["commandsUsed"] as Int,
                    doc["nsfwCommandsUsed"] as Int,
                    doc["bankBalance"] as Int,
                    doc["balance"] as Int,
                    doc["bonusXp"] as Int,
                    doc["protected"] as Boolean,
                    doc["inJail"] as Boolean,
                    doc["jailRemaining"] as Int,
                    doc["coolDownCount"] as Int
                ))
        }

        return ul

    }


    //guild

    data class Guild(

        val _id: String,
        var dropEnabled: Boolean,
        var blacklisted: Boolean,
        var ignoredChannels: MutableList<String>,
        var modMute: MutableList<String>
    )

    fun getGuild(guildId: String): Guild? {
        val doc = guilds.find(BasicDBObject("_id", guildId))
            .firstOrNull()
        return if (doc.isNullOrEmpty()) {
            val guild = Guild(
                _id = guildId,
                dropEnabled = false,
                blacklisted = false,
                ignoredChannels = ArrayList(),
                modMute = ArrayList()
            )
            setGuild(guild)
            guild
        } else {
            val guild = Guild(
                doc["_id"].toString(),
                doc.get("dropEnabled", false),
                doc.get("blacklisted", false),
                doc.get("ignoredChannels", ArrayList()),
                doc.get("modMute", ArrayList())
            )
            guild
        }
    }


    fun delGuild(guildId: String) {
        guilds.deleteOne(eq("_id", guildId))
    }

    fun setGuild(guild: Guild) {
        guilds.updateOne(
            eq("_id", guild._id),
            Document("\$set", Document.parse(Gson().toJson(guild))),
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

}
