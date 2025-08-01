package bot.boobbot.entities.db

import bot.boobbot.BoobBot
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class TransformingDatabaseField<T, R>(
    defaultValue: T,
    private val writeTransformer: (T) -> R,
    private val readTransformer: (R) -> T
) : ReadWriteProperty<User, T> {
    private var _value: T = defaultValue

    override fun getValue(thisRef: User, property: KProperty<*>): T {
        val fieldName = property.name

        val row = BoobBot.database.findOne("SELECT $fieldName FROM users_v2 WHERE userId = ?", thisRef.id)
            ?.dataDoNotAccessDirectly[fieldName]
            ?.let { readTransformer(it as R) }
            ?: return _value

        _value = row as T
        return _value
    }

    override fun setValue(thisRef: User, property: KProperty<*>, value: T) {
        val fieldName = property.name
        _value = value

        BoobBot.database.execute("INSERT INTO users_v2 (userId, $fieldName) VALUES (?, ?)" +
                " ON DUPLICATE KEY UPDATE $fieldName = VALUES($fieldName)", thisRef.id, writeTransformer(value))
    }
}
