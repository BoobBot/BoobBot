package bot.boobbot.entities.db

import bot.boobbot.BoobBot
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject


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
    fun delete() = BoobBot.database.deleteGuild(this._id)

    companion object {
        fun fromJson(json: JSONObject): Guild {
            val id = json.getString("_id")
            val prefix = json.optString("prefix", null)
            val dropEnabled = json.optBoolean("dropEnabled", false)
            val blacklisted = json.optBoolean("blacklisted", false)
            val ignoredChannels = map(json.optJSONArray("ignoredChannels")) { i, a -> a.getString(i) }.toMutableList()
            val modMute = map(json.optJSONArray("modMute")) { i, a -> a.getString(i) }.toMutableList()
            val customCommands = map(json.optJSONArray("cc")) { i, a ->
                val com = a.getJSONObject(i)
                CustomCommand(com.getString("name"), com.getString("content"))
            }.toMutableList()
            val channelDisabled = map(json.optJSONArray("channelDisabled")) { i, a ->
                val com = a.getJSONObject(i)
                DisabledCommand(com.getString("name"), com.getString("channelId"))
            }.toMutableList()
            val disabled = map(json.optJSONArray("disabled")) { i, a -> a.getString(i) }.toMutableList()

            return Guild(id, prefix, dropEnabled, blacklisted, ignoredChannels, modMute, customCommands, channelDisabled, disabled)
        }

        private fun <T> map(array: JSONArray?, transform: (Int, JSONArray) -> T): List<T> {
            if (array == null) {
                return listOf()
            }

            val list = mutableListOf<T>()
            for (i in 0 until array.length()) {
                list.add(transform(i, array))
            }
            return list
        }
    }
}
