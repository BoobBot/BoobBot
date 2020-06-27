package bot.boobbot.entities.db

import bot.boobbot.BoobBot
import java.time.Instant

data class User(
    val _id: String,
    var blacklisted: Boolean,
    var experience: Int,
    var level: Int,
    var lewdPoints: Int,
    var lewdLevel: Int,
    var messagesSent: Int,
    var nsfwMessagesSent: Int,
    var commandsUsed: Int,
    var nsfwCommandsUsed: Int,
    var bankBalance: Int,
    var balance: Int,
    var bonusXp: Int?,
    var protected: Boolean?,
    var inJail: Boolean,
    var jailRemaining: Int,
    var coolDownCount: Int,
    var lastDaily: Instant?,
    var rep: Int,
    var lastRep: Instant?,
    var lastSaved: Instant?
) {
    fun save() = BoobBot.database.setUser(this)
    fun delete() = BoobBot.database.deleteUser(this._id)

    companion object {
        fun new(userId: String): User {
            return User(
                userId, false,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                false,
                false,
                0,
                0,
                null,
                0,
                null,
                null
            )
        }
    }
}
