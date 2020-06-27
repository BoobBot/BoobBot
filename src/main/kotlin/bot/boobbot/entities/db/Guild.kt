package bot.boobbot.entities.db

import bot.boobbot.BoobBot
import com.google.gson.annotations.SerializedName


data class Guild(
    val _id: String,
    var prefix: String? = null,
    var dropEnabled: Boolean = false,
    var blacklisted: Boolean = false,
    var ignoredChannels: MutableList<String> = mutableListOf(),
    var modMute: MutableList<String> = mutableListOf(),
    @SerializedName("cc") var customCommands: MutableList<CustomCommand> = mutableListOf(),
    var channelDisabled: MutableList<DisabledCommand> = mutableListOf(),
    var disabled: MutableList<String> = mutableListOf()
) {
    fun save() = BoobBot.database.setGuild(this)
}
