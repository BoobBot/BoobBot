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


    fun getWebhook(guildId: String): String? {
        return webhooks.find(BasicDBObject("_id", guildId))
            .first()?.getString("webhook")
    }

    fun setWebhook(guildId: String, webhookUrl: String) {
        webhooks.updateOne(
            eq("_id", guildId),
            Document("\$set", Document("webhook", webhookUrl)),
            UpdateOptions().upsert(true)
        )
    }

    fun deleteWebhook(guildId: String) {
        webhooks.deleteOne(eq("_id", guildId))
    }

}