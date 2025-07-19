package bot.boobbot.entities.internals.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.Instant

class InstantAdapter : JsonSerializer<Instant>, JsonDeserializer<Instant> {
    override fun serialize(instant: Instant, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(instant.toEpochMilli())
    }

    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext?): Instant {
        return Instant.ofEpochMilli(element.asLong)
    }
}