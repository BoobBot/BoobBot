package bot.boobbot.entities.db

import bot.boobbot.BoobBot

data class Guild(
    val id: Long,
    var dropEnabled: Boolean = false,
    var blacklisted: Boolean = false,
    var premiumRedeemer: Long? = null
) {
    fun save() = BoobBot.database.setGuild(this)
    fun delete() = BoobBot.database.deleteGuild(id)
}
