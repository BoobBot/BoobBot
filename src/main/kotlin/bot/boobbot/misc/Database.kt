package bot.boobbot.misc

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.bson.Document
import org.json.JSONObject

class Database {

    private val mongo = MongoClients.create()

    private val autoPorn = mongo.getDatabase("autoporn")
    private val webhooks = autoPorn.getCollection("webhooks")

    private val bb = mongo.getDatabase("boobbott")
    private val guildSettings = bb.getCollection("settings")
    private val userSettings = bb.getCollection("usersettings")
    private val customCommands = bb.getCollection("customcoms")

//auto porn
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
// com disable
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

// nudes opt
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


    // cc
    fun addCustomCommand(guildId: String, name: String, content: String) {
        customCommands.updateOne(
            eq("_id", guildId),
            Updates.addToSet("cc", Document("name", name).append("content", content)),
            UpdateOptions().upsert(true)
        )
    }

    fun getCustomCommandListAsJsonBecauseBsonIsConfusingAndAnnoyingAsFuck(guildId: String): String? {
        val s = customCommands.find(BasicDBObject("_id", guildId))
            .firstOrNull()?.toJson()
        return s ?: "{}"
    }

    fun findCustomCommandOrNull(guildId: String, name: String): String? {
        var guildDoc = JSONObject(getCustomCommandListAsJsonBecauseBsonIsConfusingAndAnnoyingAsFuck(guildId))
        if (guildDoc.has("cc")) {
            val cc = guildDoc.getJSONArray("cc")
            for (i in 0 until cc!!.length()) {
                val com = cc.getJSONObject(i)
                if (com["name"] == name) {
                    return com.getString("content")
                }
            }
        }
        return null
    }

}