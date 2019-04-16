package bot.boobbot.misc

import bot.boobbot.BoobBot
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.bson.Document
import org.json.JSONObject

class Database {

    private val mongo = MongoClients.create()

    private val autoPorn = mongo.getDatabase("autoporn")
    private val webhooks = autoPorn.getCollection("webhooks")

    private val bb = mongo.getDatabase("boobbot")
    private val guildSettings = bb.getCollection("settings")
    private val guildPrefix = bb.getCollection("prefix")
    private val userSettings = bb.getCollection("usersettings")
    private val customCommands = bb.getCollection("customcoms")

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
       return getCustomCommands(guildId).filter { it.key == name }.getValue(name)
    }



    /**
     * Guild Prefix
     */
    fun getPrefix(guildId: String): Array<String> {
        val botPrefix = if (BoobBot.isDebug) "!bb" else "bb"
        val s = guildPrefix.find(BasicDBObject("_id", guildId))
            .firstOrNull()
        val acceptablePrefixes = arrayOf(
            botPrefix,
            "<@${BoobBot.selfId}> ",
            "<@!${BoobBot.selfId}> ",
            "<@499199815532675082>",
            "<@!499199815532675082>"
        )
        if (s != null){
            val gp = s["prefix"] as ArrayList<String>
            if (gp.size > 0) {
                return arrayOf(
                    gp[0],
                    botPrefix,
                    "<@${BoobBot.selfId}> ",
                    "<@!${BoobBot.selfId}> ",
                    "<@499199815532675082>",
                    "<@!499199815532675082>"
                )
            }
        }

        return acceptablePrefixes
    }

    fun hasPrefix(guildId: String): Boolean {
        val s = guildPrefix.find(BasicDBObject("_id", guildId))
            .firstOrNull()
        if (s != null){
            val gp = s["prefix"] as ArrayList<String>
            if (gp.size > 0) {
               return true
            }
        }
        return false
    }

    fun removePrefix(guildId: String) {
        guildPrefix.updateOne(
            eq("_id", guildId),
            Updates.popFirst("prefix"),
            UpdateOptions().upsert(true)
        )
    }

    fun setPrefix(guildId: String, prefix: String) {
        guildPrefix.updateOne(
            eq("_id", guildId),
            Updates.addToSet("prefix", prefix),
            UpdateOptions().upsert(true)
        )
    }


}