package bot.boobbot.entities.db

import bot.boobbot.BoobBot
import bot.boobbot.entities.internals.sql.SqlDatabase
import java.sql.Timestamp
import java.time.Instant

data class User(
    val _id: Long,
    var balance: Long = 0,
    var bankBalance: Long = 0,
    var blacklisted: Boolean = false,
    var bonusXp: Int = 0,
    var commandsUsed: Long = 0,
    var coolDownCount: Long = 0,
    var experience: Long = 0,
    var jailRemaining: Int = 0,
    var lastDaily: Instant? = null,
    var lastRep: Instant? = null,
    val lastSaved: Instant? = null, // DO NOT TOUCH, THE DB UPDATES THIS ON INSERT
    var level: Int = 0,
    var lewdLevel: Int = 0,
    var lewdPoints: Long = 0,
    var messagesSent: Long = 0,
    var nsfwCommandsUsed: Long = 0,
    var nsfwMessagesSent: Long = 0,
    var protected: Boolean = false,
    var rep: Long = 0,
    var anonymity: Boolean = false,
    var cockblocked: Boolean = false,
    var nudes: Boolean = false,
    var pledge: Double = 0.0,
) {
    var inDatabase: Boolean = true
        private set

    operator fun get(key: String): Any {
        return when (key) {
            "balance" -> balance
            "rep" -> rep
            "level" -> level
            else -> throw UnsupportedOperationException("Unsupported key $key")
        }
    }

    fun save() {
        BoobBot.database.setUser(this)
        inDatabase = true
    }

    fun delete() {
        BoobBot.database.deleteUser(this._id)
        inDatabase = false
    }

    companion object {
        fun withDefaults(userId: Long): User {
            return User(userId).apply { inDatabase = false }
        }

        fun fromDatabaseRow(row: SqlDatabase.Row): User {
            return User(
                row["userId"],
                row["balance"],
                row["bankBalance"],
                row["blacklisted"],
                row["bonusXp"],
                row["commandsUsed"],
                row["coolDownCount"],
                row["experience"],
                row["jailRemaining"],
                row.get<Timestamp>("lastDaily").toInstant(),
                row.get<Timestamp>("lastRep").toInstant(),
                row.get<Timestamp>("lastSaved").toInstant(),
                row["level"],
                row["lewdLevel"],
                row["lewdPoints"],
                row["messagesSent"],
                row["nsfwCommandsUsed"],
                row["nsfwMessagesSent"],
                row["protected"],
                row["rep"],
                row["anonymity"],
                row["cockblocked"],
                row["nudes"],
                row["pledge"]
            )
        }
    }
}
