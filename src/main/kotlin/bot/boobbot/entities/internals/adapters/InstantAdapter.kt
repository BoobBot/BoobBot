package bot.boobbot.entities.internals.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.Instant

class InstantAdapter : JsonSerializer<Instant>, JsonDeserializer<Instant> {
    override fun serialize(instant: Instant, type: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()

        json.add("seconds", JsonPrimitive(instant.epochSecond))
        json.add("nanos", JsonPrimitive(instant.nano))

        return json
    }

    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext?): Instant {
        return when {
            element.isJsonPrimitive -> Instant.ofEpochMilli(element.asLong)
            element.isJsonObject -> {
                val seconds = element.asJsonObject.get("seconds")
                val nanos = element.asJsonObject.get("nanos")

                if (seconds.isJsonPrimitive) {
                    if (nanos.isJsonPrimitive) {
                        return Instant.ofEpochSecond(seconds.asLong, nanos.asLong)
                    }

                    return Instant.ofEpochSecond(seconds.asLong)
                }

                throw UnsupportedOperationException()
            }
            else -> throw UnsupportedOperationException()
        }
    }
}