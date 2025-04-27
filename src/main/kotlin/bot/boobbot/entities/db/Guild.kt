package bot.boobbot.entities.db

import bot.boobbot.BoobBot

data class Guild(
    val id: Long,
    var dropEnabled: Boolean = false,
    var blacklisted: Boolean = false,
    var premiumRedeemer: Long? = null
) {
    var isNew = false
        private set

    companion object {
        fun new(id: Long): Guild {
            return Guild(id).also { it.isNew = true }
        }
    }

    fun save() = BoobBot.database.setGuild(this)
    fun delete() = BoobBot.database.deleteGuild(id)
}
