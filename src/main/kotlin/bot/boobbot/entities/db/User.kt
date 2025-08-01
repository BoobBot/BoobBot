package bot.boobbot.entities.db

import bot.boobbot.entities.internals.sql.SqlDatabase
import bot.boobbot.utils.toTimestamp
import org.slf4j.LoggerFactory
import java.sql.Timestamp
import java.time.Instant

class User(
    // default values here must match database
    // ensure the schema is set so that it CANNOT be null.
    val id: Long,
    balance: Long = 0,
    bankBalance: Long = 0,
    blacklisted: Boolean = false,
    bonusXp: Int = 0,
    commandsUsed: Long = 0,
    coolDownCount: Long = 0,
    experience: Long = 0,
    jailRemaining: Int = 0,
    lastDaily: Instant = SqlDatabase.SQL_EPOCH_SECOND.toInstant(),
    lastRep: Instant = SqlDatabase.SQL_EPOCH_SECOND.toInstant(),
    val lastSaved: Instant = SqlDatabase.SQL_EPOCH_SECOND.toInstant(), // DO NOT TOUCH, THE DB UPDATES THIS ON INSERT
    level: Int = 0,
    lewdLevel: Int = 0,
    lewdPoints: Long = 0,
    messagesSent: Long = 0,
    nsfwCommandsUsed: Long = 0,
    nsfwMessagesSent: Long = 0,
    protected: Boolean = false,
    rep: Long = 0,
    anonymity: Boolean = false,
    cockblocked: Boolean = false,
    nudes: Boolean = false,
    pledge: Double = 0.0,
) {
    var lastDaily by TransformingDatabaseField(lastDaily, Instant::toTimestamp, Timestamp::toInstant)
    var lastRep by TransformingDatabaseField(lastRep, Instant::toTimestamp, Timestamp::toInstant)

    val balance = NumberProxy(balance, id, "balance")
    val bankBalance = NumberProxy(bankBalance, id, "bankBalance")
    val bonusXp = NumberProxy(bonusXp, id, "bonusXp")
    val commandsUsed = NumberProxy(commandsUsed, id, "commandsUsed")
    val coolDownCount = NumberProxy(coolDownCount, id, "coolDownCount")
    val experience = NumberProxy(experience, id, "experience")
    val jailRemaining = NumberProxy(jailRemaining, id, "jailRemaining")
    val level = NumberProxy(level, id, "level")
    val lewdLevel = NumberProxy(lewdLevel, id, "lewdLevel")
    val lewdPoints = NumberProxy(lewdPoints, id, "lewdPoints")
    val messagesSent = NumberProxy(messagesSent, id, "messagesSent")
    val nsfwCommandsUsed = NumberProxy(nsfwCommandsUsed, id, "nsfwCommandsUsed")
    val nsfwMessagesSent = NumberProxy(nsfwMessagesSent, id, "nsfwMessagesSent")
    val rep = NumberProxy(rep, id, "rep")
    val pledge = NumberProxy(pledge, id, "pledge")

    var blacklisted by DatabaseField(blacklisted)
    var protected by DatabaseField(protected)
    var anonymity by DatabaseField(anonymity)
    var cockblocked by DatabaseField(cockblocked)
    var nudes by DatabaseField(nudes)

    operator fun get(key: String): Any {
        return when (key) {
            "balance" -> balance
            "rep" -> rep
            "level" -> level
            else -> throw UnsupportedOperationException("Unsupported key $key")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(User::class.java)

        fun withDefaults(userId: Long): User {
            return User(userId)
        }

        fun fromDatabaseRow(row: SqlDatabase.Row): User {
            try {
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
            } catch (t: IllegalStateException) {
                log.error(
                    "Malformed data for {} (all keys: {})",
                    row.getOrNull<String>("userId") ?: "MISSING USER ID",
                    row.dataDoNotAccessDirectly.keys.joinToString(", "),
                    t
                )

                throw t
            }
        }
    }
}
