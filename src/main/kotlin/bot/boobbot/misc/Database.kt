package bot.boobbot.misc

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
    private val userSettings = bb.getCollection("usersettings")


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

}