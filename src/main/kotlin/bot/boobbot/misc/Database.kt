package bot.boobbot.misc

import bot.boobbot.BoobBot
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.bson.Document

class Database {

    private val mongo = MongoClients.create()

    private val autoPorn = mongo.getDatabase("autoporn")
    private val webhooks = autoPorn.getCollection("webhooks")

    private val bb = mongo.getDatabase("boobbot")
    private val guildSettings = bb.getCollection("settings")
    private val guildPrefix = bb.getCollection("prefix")
    private val userSettings = bb.getCollection("usersettings")
    private val customCommands = bb.getCollection("customcoms")
    private val donor = bb.getCollection("donor")


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
     * Disable-able commands
     */
    fun getDisabledCommands(guildId: String): List<String> {
        val s = guildSettings.find(BasicDBObject("_id", guildId))
            .firstOrNull() ?: return emptyList()

        return s.getList("disabled", String::class.java)
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

    /**
     * Nudes opt-in/out
     */
    fun setUserCanReceiveNudes(userId: String, canReceive: Boolean) {
        val newSetting = Document("nudes", canReceive)

        userSettings.updateOne(
            eq("_id", userId),
            Document("\$set", newSetting),
            UpdateOptions().upsert(true)
        )
    }

    fun getCanUserReceiveNudes(userId: String): Boolean {
        return userSettings.find(BasicDBObject("_id", userId))
            .firstOrNull()?.getBoolean("nudes")
            ?: false
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

    fun hasPrefix(guildId: String): Boolean {
        return guildPrefix.find(BasicDBObject("_id", guildId)).count() > 0
    }

    fun removePrefix(guildId: String) {
        guildPrefix.deleteOne(eq("_id", guildId))
    }

    fun setPrefix(guildId: String, prefix: String) {
        guildPrefix.updateOne(
            eq("_id", guildId),
            Document("\$set", prefix),
            UpdateOptions().upsert(true)
        )
    }

    /**
     * Donor Stuff
     */
    fun getDonor(userId: String): Double {
        return donor.find(BasicDBObject("_id", userId))
            .firstOrNull()
            ?.getDouble("pledge")
            ?: 0.0
    }

    fun setDonor(userId: String, pledge: Double) {
        val doc = Document("pledge", pledge)

        donor.updateOne(
            eq("_id", userId),
            Document("\$set", doc),
            UpdateOptions().upsert(true)
        )
    }

    fun removeDonor(userId: String) {
        donor.deleteOne(eq("_id", userId))
    }


}