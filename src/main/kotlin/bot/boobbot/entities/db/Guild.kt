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
    init {
        var update = false

        if (ignoredChannels == null) {
            ignoredChannels = mutableListOf()
            update = true
        }

        if (modMute == null) {
            modMute = mutableListOf()
            update = true
        }

        if (channelDisabled == null) {
            channelDisabled = mutableListOf()
            update = true
        }

        if (disabled == null) {
            disabled = mutableListOf()
            update = true
        }

        if (customCommands == null) {
            customCommands = mutableListOf()
            update = true
        }

        if (update) {
            save()
        }
    }
    fun save() = BoobBot.database.setGuild(this)
    fun delete() = BoobBot.database.deleteGuild(this._id)
}
