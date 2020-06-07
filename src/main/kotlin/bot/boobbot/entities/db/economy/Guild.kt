package bot.boobbot.entities.db.economy

import bot.boobbot.BoobBot


data class Guild(
    val _id: String,
    var dropEnabled: Boolean,
    var blacklisted: Boolean,
    var ignoredChannels: MutableList<String>,
    var modMute: MutableList<String>
) {

    fun save() = BoobBot.database.setGuild(this)

    companion object {
        fun new(guildId: String): Guild {
            return Guild(
                guildId,
                false,
                false,
                mutableListOf(),
                mutableListOf()
            )
        }
    }
}
