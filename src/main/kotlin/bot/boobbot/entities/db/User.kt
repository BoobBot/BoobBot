package bot.boobbot.entities.db

import bot.boobbot.BoobBot
import java.time.Instant

data class User(
    val _id: String,
    var balance: Long = 0,
    var bankBalance: Int = 0,
    var blacklisted: Boolean = false,
    var bonusXp: Int = 0,
    var commandsUsed: Int = 0,
    var coolDownCount: Int = 0,
    var experience: Int = 0,
    var inJail: Boolean = false,
    var jailRemaining: Int = 0,
    var lastDaily: Instant? = null,
    var lastRep: Instant? = null,
    //var lastSaved: Instant? = null { seconds: long, nanos: long }
    var level: Int = 0,
    var lewdLevel: Int = 0,
    var lewdPoints: Int = 0,
    var messagesSent: Int = 0,
    var nsfwCommandsUsed: Int = 0,
    var nsfwMessagesSent: Int = 0,
    var protected: Boolean = false,
    var rep: Int = 0,
    var anonymity: Boolean = false,
    var cockblocked: Boolean = false,
    var nudes: Boolean = false
) {
    fun save() = BoobBot.database.setUser(this)
    fun delete() = BoobBot.database.deleteUser(this._id)
}
