package bot.boobbot.models

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import org.bson.Document

class Database {

    private val mongo = MongoClients.create()

    private val autoPorn = mongo.getDatabase("autoporn")
    private val webhooks = autoPorn.getCollection("webhooks")


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

}