package bot.boobbot.entities.db

import bot.boobbot.BoobBot

@Suppress("UNCHECKED_CAST")
class NumberProxy<T : Number>(value: T, private val userId: Long, private val key: String) {
    var value: T = value
        private set

    private fun one(): T = when (value) {
        is Int -> 1 as T
        is Long -> 1L as T
        is Float -> 1f as T
        is Double -> 1.0 as T
        else -> throw UnsupportedOperationException("Numerical type ${value::class} is not unsupported")
    }

    operator fun plus(other: T): T {
        return when (other) {
            is Int -> (value.toInt() + other) as T
            is Long -> (value.toLong() + other) as T
            is Float -> (value.toFloat() + other) as T
            is Double -> (value.toDouble() + other) as T
            else -> throw UnsupportedOperationException("Numerical type ${other::class} is not supported")
        }
    }

    operator fun minus(other: T): T {
        return when (other) {
            is Int -> (value.toInt() - other) as T
            is Long -> (value.toLong() - other) as T
            is Float -> (value.toFloat() - other) as T
            is Double -> (value.toDouble() - other) as T
            else -> throw UnsupportedOperationException("Numerical type ${other::class} is not supported")
        }
    }

    operator fun div(other: T): T {
        return when (other) {
            is Int -> (value.toInt() / other) as T
            is Long -> (value.toLong() / other) as T
            is Float -> (value.toFloat() / other) as T
            is Double -> (value.toDouble() / other) as T
            else -> throw UnsupportedOperationException("Numerical type ${other::class} is not supported")
        }
    }

    operator fun plusAssign(other: T) {
        value = plus(other)

        BoobBot.database.execute("INSERT INTO users_v2 (userId, $key) VALUES (?, ?)" +
                " ON DUPLICATE KEY UPDATE $key = $key + ?", userId, value, other)
    }

    operator fun minusAssign(other: T) {
        value = minus(other)

        BoobBot.database.execute("INSERT INTO users_v2 (userId, $key) VALUES (?, ?)" +
                " ON DUPLICATE KEY UPDATE $key = $key - ?", userId, value, other)
    }

    operator fun inc(): NumberProxy<T> {
        this += one()
        return this
    }

    operator fun dec(): NumberProxy<T> {
        this -= one()
        return this
    }

    operator fun compareTo(other: T): Int {
        return when (other) {
            is Int -> value.toInt().compareTo(other)
            is Long -> value.toLong().compareTo(other)
            is Float -> value.toFloat().compareTo(other)
            is Double -> value.toDouble().compareTo(other)
            else -> throw UnsupportedOperationException("Numerical type ${other::class} is not supported")
        }
    }

    fun set(newValue: T) {
        value = newValue

        BoobBot.database.execute("INSERT INTO users_v2 (userId, $key) VALUES (?, ?)" +
                " ON DUPLICATE KEY UPDATE $key = VALUES($key)", userId, newValue)
    }

    override fun toString(): String {
        return value.toString()
    }
}
