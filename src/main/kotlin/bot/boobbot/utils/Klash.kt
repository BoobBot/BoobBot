package bot.boobbot.utils

import org.json.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object Klash {
    inline fun <reified T : Any> construct(getter: (String) -> Any?, default: (KParameter) -> Any?): T {
        val constructor = T::class.primaryConstructor!!
        val params = constructor.parameters
        val args = hashMapOf<KParameter, Any?>()

        for (param in params) {
            val name = param.name ?: continue
            val value = getter(name)

            if (value == null) {
                if (param.isOptional) {
                    continue
                }

                if (param.type.isMarkedNullable) {
                    args[param] = null
                }

                val defaultValue = default(param)
                    ?: throw IllegalStateException("Could not specify a value for parameter $name")

                args[param] = defaultValue
            } else {
                args[param] = value
            }
        }

        return constructor.callBy(args)
    }

    /**
     * Constructs the JSON string into an object of the given type.
     */
//    inline fun <reified T : Any> construct(json: String): T {
//        val obj = JSONObject(json)
//        val constructor = T::class.primaryConstructor!!
//        val params = constructor.parameters
//        val args = hashMapOf<KParameter, Any?>()
//
//        for (param in params) {
//            val name = param.name
//
//            if (!obj.has(name)) {
//                if (param.isOptional) {
//                    continue
//                }
//
//                if (param.type.isMarkedNullable) {
//                    args[param] = null
//                }
//
//                throw IllegalStateException("Could not specify a value for parameter $name")
//            } else {
//                if (param.type.jvmErasure.javaObjectType.isAssignableFrom(List::class.java)) {
//                    val listType = param.type.arguments.first()
//                    val jvmType = listType.type?.jvmErasure?.javaObjectType
//
//                    val list = obj.getJSONArray(name)
//                        .filter { jvmType?.let(it::class.java::isAssignableFrom) ?: true }
//
//                    args[param] = list
//                } else {
//                    args[param] = value
//                }
//
//                // Consider HashMap support (JSONObject -> <*, *>)
//            }
//        }
//
//        return constructor.callBy(args)
//    }

    /**
     * Deconstructs the given object into a JSON string.
     */
    @Suppress("UNCHECKED_CAST")
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
