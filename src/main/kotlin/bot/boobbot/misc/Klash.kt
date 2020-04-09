package bot.boobbot.misc

import org.json.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

object Klash {
    /**
     * Constructs the JSON string into an object of the given type.
     */
    inline fun <reified T : Any> construct(json: String): T {
        val obj = JSONObject(json)
        val constructor = T::class.primaryConstructor!!
        val params = constructor.parameters
        val args = hashMapOf<KParameter, Any?>()

        for (param in params) {
            val name = param.name

            if (!obj.has(name)) {
                if (param.isOptional) {
                    continue
                }

                if (param.type.isMarkedNullable) {
                    args[param] = null
                }

                throw IllegalStateException("Could not specify a value for parameter $name")
            } else {
                if (param.type.jvmErasure.javaObjectType.isAssignableFrom(List::class.java)) {
                    val listType = param.type.arguments.first()
                    val jvmType = listType.type?.jvmErasure?.javaObjectType

                    val list = obj.getJSONArray(name)
                        .filter { jvmType?.let(it::class.java::isAssignableFrom) ?: true }

                    args[param] = list
                } else {
                    args[param] = obj.get(name)
                }

                // Consider HashMap support (JSONObject -> <*, *>)
            }
        }

        return constructor.callBy(args)
    }

    /**
     * Deconstructs the given object into a JSON string.
     */
    fun <T : Any> deconstruct(obj: T): String {
        val properties = (obj::class as KClass<T>).memberProperties
        val json = JSONObject()

        for (prop in properties) {
            val value = prop.get(obj)
            json.put(prop.name, value)
        }

        return json.toString()
    }
}
