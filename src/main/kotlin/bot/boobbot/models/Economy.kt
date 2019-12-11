package bot.boobbot.models

import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import java.time.Instant
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt


data class User(
    val userId: String,
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
    // guild lvl for roles
    var guilds: List<Guilds>?,
    var inventory: Inventory?,
    var lastSeen: Instant?


)

data class Guild(
    val id: String,
    var experience: Int,
    var level: Int
)

data class Guilds(
    var guilds: List<Guild>
)

data class Inventory(
    val items: List<Item>?
)

data class StoreInventory(
    val items: List<StoreItem>?
)

data class StoreItem(
    val item: Item,
    val price: Int,
    val onSale: Boolean,
    val saleDiscount: Float
)


data class Item(
    val name: String,
    val isUnique: Boolean,
    val maxUses: Int?,
    val emote: String,
    // for dashboard later
    val emoteImageBlob: String,
    val price: Int,
    // call a fun?
    val function: String
)


fun calculateLewdLevel(user: bot.boobbot.models.User): Int {
    val calculateLewdPoints =
                (user.experience / 100) * .1 +
                (user.nsfwCommandsUsed / 100) * .3 -
                (user.commandsUsed / 100) * .3 +
                (user.lewdPoints / 100) * 20
    // lewd level up
   return floor(0.1 * sqrt(calculateLewdPoints.toDouble())).toInt()
}


fun handleMessage(ctx: Context, user: bot.boobbot.models.User) {
    if (user.blacklisted) {
        return
    }
    if (user.inJail) {
        user.jailRemaining = min(user.jailRemaining - 1, 0)
        if (user.jailRemaining == 0){
            user.inJail = false
        }
        //TODO save user
        return
    }

    if (user.coolDownCount >= (0..10).random()) {
        user.coolDownCount = 0
        user.messagesSent = user.messagesSent + 1

        if (ctx.textChannel!!.isNSFW) {
            val tags = Formats.tag
            val tagSize = tags.filter { ctx.message.contentDisplay.contains(it.toString()) }.size
            user.lewdPoints = user.lewdPoints + min(tagSize, 5) * (user.balance / 100) * .01.toInt()
            user.nsfwMessagesSent = user.nsfwMessagesSent+1
        }
        user.experience = if (user.bonusXp != null && user.bonusXp!! > 0) user.experience + 2 else user.experience + 1
        if (user.bonusXp != null && user.bonusXp!! > 0) {
            user.bonusXp = user.bonusXp!! - 1
        }

    }
    user.level = floor(0.1 * sqrt(user.experience.toDouble())).toInt()
    user.lewdLevel = calculateLewdLevel(user)
    //TODO save user
}


